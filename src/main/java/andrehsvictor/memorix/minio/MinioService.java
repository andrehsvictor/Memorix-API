package andrehsvictor.memorix.minio;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    private static final String BUCKET_NAME = "memorix";

    public String upload(MultipartFile file) {
        try {
            String filename = generateFilename(file);
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(filename)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            minioClient.putObject(args);
            GetPresignedObjectUrlArgs urlArgs = GetPresignedObjectUrlArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(filename)
                    .build();
            return minioClient.getPresignedObjectUrl(urlArgs);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    private String generateFilename(MultipartFile file) {
        return System.currentTimeMillis() + "." + file.getContentType().split("/")[1];
    }
}
