package andrehsvictor.memorix;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import io.restassured.RestAssured;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    private static final String POSTGRESQL_IMAGE = "postgres:alpine";

    private static final String REDIS_IMAGE = "redis:alpine";

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    protected RedisTemplate<String, Long> redisTemplate;

    @Container
    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(POSTGRESQL_IMAGE))
            .withDatabaseName("memorix")
            .withUsername("memorix")
            .withPassword("memorix");

    @Container
    @SuppressWarnings("resource")
    private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(
            DockerImageName.parse(REDIS_IMAGE))
            .withExposedPorts(6379);

    @LocalServerPort
    private int port;

    @BeforeAll
    static void beforeAll() {
        POSTGRESQL_CONTAINER.start();
        REDIS_CONTAINER.start();

        System.setProperty("spring.datasource.url", POSTGRESQL_CONTAINER.getJdbcUrl());
        System.setProperty("spring.datasource.username", POSTGRESQL_CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", POSTGRESQL_CONTAINER.getPassword());
        System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
        System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());

        assertThat(POSTGRESQL_CONTAINER.isRunning()).isTrue();
        assertThat(REDIS_CONTAINER.isRunning()).isTrue();
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = String.format("http://localhost:%d", port);
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        redisTemplate.opsForValue().set("user:nextId", 1L);
    }

    protected void clearAll(Class<?>... entities) {
        for (Class<?> entity : entities) {
            String entityName;
            if (entity.getAnnotation(Entity.class).name().isBlank()) {
                entityName = entity.getSimpleName();
            } else {
                entityName = entity.getAnnotation(Entity.class).name();
            }
            String query = String.format("DELETE FROM %s", entityName);
            entityManager.createQuery(query).executeUpdate();
        }
    }

}
