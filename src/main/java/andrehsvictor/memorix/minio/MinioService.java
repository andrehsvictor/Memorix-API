package andrehsvictor.memorix.minio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.default-bucket:memorix}")
    private String defaultBucket = "memorix";

    @Value("${minio.url:http://localhost:9000}")
    private String url = "http://localhost:9000";

    public String upload(MultipartFile file) {
        try {
            String filename = generateFilename(file);
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(defaultBucket)
                    .object(filename)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            minioClient.putObject(args);
            return generateUrl(filename);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    private String generateUrl(String filename) {
        return url + "/" + defaultBucket + "/" + filename;
    }

    private String generateFilename(MultipartFile file) {
        return System.currentTimeMillis() + "." + file.getContentType().split("/")[1];
    }
}
