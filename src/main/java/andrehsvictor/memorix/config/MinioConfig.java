package andrehsvictor.memorix.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class MinioConfig {

    @Value("${minio.url:http://localhost:9000}")
    private String endpoint = "http://localhost:9000";

    @Value("${minio.root-user:root}")
    private String rootUser = "root";

    @Value("${minio.root-password:root}")
    private String rootPassword = "root";

    @Bean
    MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(rootUser, rootPassword)
                .build();
    }
}
