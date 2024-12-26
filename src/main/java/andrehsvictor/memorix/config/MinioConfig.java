package andrehsvictor.memorix.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import jakarta.validation.constraints.Size;

@Configuration
@Validated
public class MinioConfig {

    @Value("${minio.url:http://localhost:9000}")
    private String endpoint = "http://localhost:9000";

    @Size(min = 3, message = "Root username must be at least 3 characters long")
    @Value("${minio.root-user:root}")
    private String rootUser = "root";

    @Size(min = 8, message = "Root password must be at least 8 characters long")
    @Value("${minio.root-password:root}")
    private String rootPassword = "root";

    @Value("${minio.default-bucket:memorix}")
    private String defaultBucket = "memorix";

    @Bean
    MinioClient minioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(rootUser, rootPassword)
                .build();
        try {
            BucketExistsArgs args = BucketExistsArgs.builder()
                    .bucket(defaultBucket)
                    .build();
            if (!minioClient.bucketExists(args)) {
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                        .bucket(defaultBucket)
                        .build();
                minioClient.makeBucket(makeBucketArgs);
            }
            SetBucketPolicyArgs policyArgs = SetBucketPolicyArgs.builder()
                    .bucket(defaultBucket)
                    .config("public-read")
                    .build();
            minioClient.setBucketPolicy(policyArgs);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create default bucket", e);
        }
        return minioClient;
    }
}
