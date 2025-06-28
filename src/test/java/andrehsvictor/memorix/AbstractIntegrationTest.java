package andrehsvictor.memorix;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import io.restassured.RestAssured;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:alpine")
            .withReuse(true);

    private static final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:management-alpine")
            .withReuse(true);

    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest").withReuse(true);

    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:alpine")
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort())
            .withReuse(true);

    private static final GenericContainer<?> mailhogContainer = new GenericContainer<>("mailhog/mailhog:latest")
            .withExposedPorts(8025, 1025)
            .withEnv("MH_STORAGE", "memory")
            .withReuse(true);

    private static final GenericContainer<?> minioContainer = new GenericContainer<>("quay.io/minio/minio:latest")
            .withExposedPorts(9000, 9001)
            .withEnv("MINIO_ROOT_USER", "minioadmin")
            .withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
            .withCommand("server", "/data", "--console-address", ":9001")
            .withReuse(true);

    @LocalServerPort
    private Integer port;

    @BeforeAll
    private static void beforeAll() {
        postgreSQLContainer.start();
        rabbitMQContainer.start();
        mongoDBContainer.start();
        redisContainer.start();
        mailhogContainer.start();
        minioContainer.start();

        assertThat(postgreSQLContainer.isRunning()).isTrue();
        assertThat(rabbitMQContainer.isRunning()).isTrue();
        assertThat(mongoDBContainer.isRunning()).isTrue();
        assertThat(redisContainer.isRunning()).isTrue();
        assertThat(mailhogContainer.isRunning()).isTrue();
        assertThat(minioContainer.isRunning()).isTrue();
    }

    @DynamicPropertySource
    private static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
        registry.add("spring.mail.host", mailhogContainer::getHost);
        registry.add("spring.mail.port", () -> mailhogContainer.getMappedPort(1025).toString());

        registry.add("memorix.minio.endpoint",
                () -> "http://" + minioContainer.getHost() + ":" + minioContainer.getMappedPort(9000));
        registry.add("memorix.minio.access-key", () -> "minioadmin");
        registry.add("memorix.minio.secret-key", () -> "minioadmin");
        registry.add("memorix.minio.bucket-name", () -> "memorix");
    }

    @BeforeEach
    private void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    protected String getMailhogUrl() {
        return "http://" + mailhogContainer.getHost() + ":" + mailhogContainer.getMappedPort(8025);
    }

    protected String getMinioUrl() {
        return "http://" + minioContainer.getHost() + ":" + minioContainer.getMappedPort(9000);
    }

    protected String getRabbitMQUrl() {
        return "http://" + rabbitMQContainer.getHost() + ":" + rabbitMQContainer.getMappedPort(15672);
    }

}