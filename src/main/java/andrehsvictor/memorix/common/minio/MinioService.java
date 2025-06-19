package andrehsvictor.memorix.common.minio;

import java.net.URI;
import java.time.Instant;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String upload(MultipartFile file) {
        try {
            String fileName = generateFileName(file.getOriginalFilename());

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return buildFileUrl(fileName);
        } catch (Exception e) {
            log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to upload file to Minio", e);
        }
    }

    @RabbitListener(queues = "minio.v1.delete")
    public void delete(String url) {
        try {
            validateMinioUrl(url);
            String[] pathSegments = extractPathFromUrl(url);

            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(pathSegments[0])
                    .object(pathSegments[1])
                    .build());

            log.info("Successfully deleted file: {}", url);
        } catch (IllegalArgumentException e) {
            return;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from Minio", e);
        }
    }

    private String generateFileName(String originalFilename) {
        return Instant.now().toEpochMilli() + "-" + originalFilename;
    }

    private String buildFileUrl(String fileName) {
        return String.format("%s/%s/%s",
                minioProperties.getEndpoint(),
                minioProperties.getBucketName(),
                fileName);
    }

    private void validateMinioUrl(String url) {
        if (!url.startsWith(minioProperties.getEndpoint())) {
            throw new IllegalArgumentException("URL does not belong to Minio: " + url);
        }
    }

    private String[] extractPathFromUrl(String url) {
        try {
            String path = URI.create(url).getPath();
            String[] segments = path.substring(1).split("/");

            if (segments.length < 2) {
                throw new IllegalArgumentException("Invalid Minio URL format: " + url);
            }

            return new String[] { segments[0], segments[1] };
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL format: " + url, e);
        }
    }
}