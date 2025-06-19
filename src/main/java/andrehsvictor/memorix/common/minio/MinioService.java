package andrehsvictor.memorix.common.minio;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String upload(MultipartFile file, Map<String, String> metadata) {
        try {
            String fileName = generateFileName(file.getOriginalFilename());
            String bucketName = minioProperties.getBucketName();

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .userMetadata(metadata)
                    .contentType(file.getContentType())
                    .build());

            return buildFileUrl(fileName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to Minio", e);
        }
    }

    @RabbitListener(queues = "minio.v1.delete")
    public void delete(String url) {
        if (!isValidMinioUrl(url)) {
            log.warn("Invalid Minio URL, skipping deletion: {}", url);
            return;
        }

        try {
            String objectName = extractObjectNameFromUrl(url);
            deleteObject(objectName);
            log.info("Successfully deleted file: {}", url);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from Minio", e);
        }
    }

    @RabbitListener(queues = "minio.v1.delete.metadata")
    public void deleteByMetadata(Map<String, String> metadata) {
        try {
            String bucketName = minioProperties.getBucketName();

            minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build())
                    .forEach(item -> {
                        try {
                            String objectName = item.get().objectName();
                            if (hasMatchingMetadata(objectName, metadata)) {
                                deleteObject(objectName);
                            }
                        } catch (Exception e) {
                            log.error("Error processing object during metadata deletion: {}", e.getMessage(), e);
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete files with metadata from Minio", e);
        }
    }

    private void deleteObject(String objectName) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .object(objectName)
                .build());
    }

    private boolean hasMatchingMetadata(String objectName, Map<String, String> targetMetadata)
            throws Exception {
        Map<String, String> objectMetadata = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(objectName)
                        .build())
                .userMetadata();

        return targetMetadata.entrySet().stream()
                .allMatch(entry -> entry.getValue().equals(objectMetadata.get(entry.getKey())));
    }

    private boolean isValidMinioUrl(String url) {
        return url != null && url.startsWith(minioProperties.getEndpoint());
    }

    private String extractObjectNameFromUrl(String url) {
        try {
            String path = URI.create(url).getPath();
            String[] segments = path.substring(1).split("/", 2);

            if (segments.length < 2) {
                throw new IllegalArgumentException("Invalid Minio URL format: " + url);
            }

            return segments[1]; // Only return the object name
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL format: " + url, e);
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
}