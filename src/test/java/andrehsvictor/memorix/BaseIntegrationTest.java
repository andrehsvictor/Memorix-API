package andrehsvictor.memorix;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.util.PasswordUtil;
import io.restassured.RestAssured;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import net.datafaker.Faker;

@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    private static final String POSTGRESQL_IMAGE = "postgres:alpine";

    private static final String REDIS_IMAGE = "redis:alpine";

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    protected RedisTemplate<String, Long> redisTemplate;

    protected Faker faker = new Faker();

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
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort());

    @LocalServerPort
    private int port;

    @BeforeAll
    static void beforeAll() {
        POSTGRESQL_CONTAINER.start();
        REDIS_CONTAINER.start();

        assertThat(POSTGRESQL_CONTAINER.isRunning()).isTrue();
        assertThat(REDIS_CONTAINER.isRunning()).isTrue();
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = String.format("http://localhost:%d", port);
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
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

    protected User createRandomUserInDb() {
        User user = new User();
        user.setUsername(faker.internet().username());
        user.setEmail(faker.internet().emailAddress());
        user.setDisplayName(faker.name().fullName());
        String password = faker.internet().password();
        user.setPassword(PasswordUtil.hash(password));
        entityManager.persist(user);
        user.setPassword(password);
        return user;
    }

}
