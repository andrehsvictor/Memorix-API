package andrehsvictor.memorix.common.minio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import andrehsvictor.memorix.common.util.FileUtil;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {

    private final MinioProperties minioProperties;
    private final FileUtil fileUtil;

    @Bean
    MinioClient minioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();

        setupBucket(minioClient);
        setupBucketPolicy(minioClient);

        return minioClient;
    }

    private void setupBucket(MinioClient minioClient) {
        try {
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .build();

            if (!minioClient.bucketExists(bucketExistsArgs)) {
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .build();
                minioClient.makeBucket(makeBucketArgs);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bucket", e);
        }
    }

    private void setupBucketPolicy(MinioClient minioClient) {
        try {
            String bucketPolicy = fileUtil.readFileAsString("classpath:/minio/bucket-policy.json");
            bucketPolicy = bucketPolicy
                    .replace("{{bucketName}}", minioProperties.getBucketName());
            SetBucketPolicyArgs setBucketPolicyArgs = SetBucketPolicyArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .config(bucketPolicy)
                    .build();
            minioClient.setBucketPolicy(setBucketPolicyArgs);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set bucket policy", e);
        }
    }
}