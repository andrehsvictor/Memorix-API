package andrehsvictor.memorix.account;

import static io.restassured.RestAssured.given;
// assertThat() from AssertJ below
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import andrehsvictor.memorix.IntegrationTest;
import andrehsvictor.memorix.account.dto.AccountDto;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.dto.CreateUserDto;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import net.datafaker.Faker;

@Transactional
@DisplayName("Account Creation Integration Test")
class AccountCreationIT extends IntegrationTest {

    private static final String PATH = "/api/v1/account";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Faker faker = new Faker();

    private CreateUserDto minimalCreateUserDto() {
        return CreateUserDto.builder()
                .username(faker.internet()
                        .username()
                        .toLowerCase()
                        .replace("-", "_")
                        .replace(".", ""))
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .displayName(faker.name().fullName())
                .build();
    }

    private CreateUserDto fullCreateUserDto() {
        return CreateUserDto.builder()
                .username(faker.internet()
                        .username()
                        .toLowerCase()
                        .replace("-", "_")
                        .replace(".", ""))
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .displayName(faker.name().fullName())
                .bio(faker.lorem().sentence())
                .pictureUrl(faker.internet().image())
                .build();
    }

    private boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private ValidatableResponse createAccount(CreateUserDto createUserDto) {
        return given()
                .contentType(ContentType.JSON)
                .body(createUserDto)
                .when()
                .post(PATH)
                .then();
    }

    @AfterEach
    void tearDown() {
        entityManager.createQuery("DELETE FROM User").executeUpdate();
    }

    @Test
    @DisplayName("Should create account when given minimal user data")
    void givenMinimalCreateUserDto_whenCreate_thenAccountCreated() {
        CreateUserDto createUserDto = minimalCreateUserDto();

        AccountDto accountDto = createAccount(createUserDto)
                .statusCode(201)
                .body("id", isA(Integer.class))
                .body("username", equalTo(createUserDto.getUsername()))
                .body("email", equalTo(createUserDto.getEmail()))
                .body("displayName", equalTo(createUserDto.getDisplayName()))
                .body("bio", equalTo(null))
                .body("pictureUrl", equalTo(null))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue())
                .time(lessThan(700L))
                .extract()
                .as(AccountDto.class);

        User user = entityManager.find(User.class, accountDto.getId());

        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(createUserDto.getUsername());
        assertThat(user.getEmail()).isEqualTo(createUserDto.getEmail());
        assertThat(user.getDisplayName()).isEqualTo(createUserDto.getDisplayName());
        assertThat(passwordMatches(createUserDto.getPassword(), user.getPassword())).isTrue();
        assertThat(user.getBio()).isNull();
        assertThat(user.getPictureUrl()).isNull();
        assertThat(user.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(user.getUpdatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create account when given full user data")
    void givenFullCreateUserDto_whenCreate_thenAccountCreated() {
        CreateUserDto createUserDto = fullCreateUserDto();

        AccountDto accountDto = createAccount(createUserDto)
                .statusCode(201)
                .body("id", isA(Integer.class))
                .body("username", equalTo(createUserDto.getUsername()))
                .body("email", equalTo(createUserDto.getEmail()))
                .body("displayName", equalTo(createUserDto.getDisplayName()))
                .body("bio", equalTo(createUserDto.getBio()))
                .body("pictureUrl", equalTo(createUserDto.getPictureUrl()))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue())
                .time(lessThan(500L))
                .extract()
                .as(AccountDto.class);

        User user = entityManager.find(User.class, accountDto.getId());

        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo(createUserDto.getUsername());
        assertThat(user.getEmail()).isEqualTo(createUserDto.getEmail());
        assertThat(user.getDisplayName()).isEqualTo(createUserDto.getDisplayName());
        assertThat(passwordMatches(createUserDto.getPassword(), user.getPassword())).isTrue();
        assertThat(user.getBio()).isEqualTo(createUserDto.getBio());
        assertThat(user.getPictureUrl()).isEqualTo(createUserDto.getPictureUrl());
        assertThat(user.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(user.getUpdatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should not create account when username is already taken")
    void givenExistingUsername_whenCreate_thenConflict() {
        CreateUserDto createUserDto = minimalCreateUserDto();

        createAccount(createUserDto)
                .statusCode(201);

        createAccount(createUserDto)
                .statusCode(409)
                .body("errors", contains("Username or email already in use"));
    }

    @Test
    @DisplayName("Should not create account when email is already taken")
    void givenExistingEmail_whenCreate_thenConflict() {
        CreateUserDto createUserDto = minimalCreateUserDto();

        createAccount(createUserDto)
                .statusCode(201);

        createUserDto.setUsername(faker.internet().username()
                .toLowerCase()
                .replace("-", "_")
                .replace(".", ""));

        createAccount(createUserDto)
                .statusCode(409)
                .body("errors", contains("Username or email already in use"));
    }

    @Test
    @DisplayName("Should not create account when username and email are already taken")
    void givenExistingUsernameAndEmail_whenCreate_thenConflict() {
        CreateUserDto createUserDto = minimalCreateUserDto();

        createAccount(createUserDto)
                .statusCode(201);

        createAccount(createUserDto)
                .statusCode(409)
                .body("errors", contains("Username or email already in use"));
    }

    @Test
    @DisplayName("Should not create account when are there null fields")
    void givenNullFields_whenCreate_thenBadRequest() {
        CreateUserDto createUserDto = minimalCreateUserDto();
        createUserDto.setUsername(null);

        createAccount(createUserDto)
                .statusCode(400)
                .body("errors", contains(Map.of("field", "username", "message", "Username is required")));

        createUserDto = minimalCreateUserDto();
        createUserDto.setEmail(null);

        createAccount(createUserDto)
                .statusCode(400)
                .body("errors", contains(Map.of("field", "email", "message", "Email is required")));

        createUserDto = minimalCreateUserDto();
        createUserDto.setPassword(null);

        createAccount(createUserDto)
                .statusCode(400)
                .body("errors", contains(Map.of("field", "password", "message", "Password is required")));

        createUserDto = minimalCreateUserDto();
        createUserDto.setDisplayName(null);

        createAccount(createUserDto)
                .statusCode(400)
                .body("errors", contains(Map.of("field", "displayName", "message", "Display name is required")));

        createUserDto = new CreateUserDto();

        createAccount(createUserDto)
                .statusCode(400)
                .body("errors", contains(
                        Map.of("field", "username", "message", "Username is required"),
                        Map.of("field", "password", "message", "Password is required"),
                        Map.of("field", "email", "message", "Email is required"),
                        Map.of("field", "displayName", "message", "Display name is required")));

    }

    @Test
    @DisplayName("Should not create account when username is invalid")
    void givenInvalidUsername_whenCreate_thenBadRequest() {
        CreateUserDto createUserDto = minimalCreateUserDto();
        createUserDto.setUsername("invalid-username");

        createAccount(createUserDto)
                .statusCode(400)
                .body("errors",
                        containsString("Username must contain only lowercase letters, numbers and underscores"));

        createUserDto.setUsername("a");

        createAccount(createUserDto)
                .statusCode(400)
                .body("errors", containsString("Username must be between 3 and 20 characters"));

        createUserDto.setUsername("aaaaaaaaaaaaaaaaaaaaaaaaa");

        createAccount(createUserDto)
                .statusCode(400)
                .body("errors", containsString("Username must be between 3 and 20 characters"));
    }

    @Test
    @DisplayName("Should not create account when email is invalid")
    void givenInvalidEmail_whenCreate_thenBadRequest() {
        CreateUserDto createUserDto = minimalCreateUserDto();
        createUserDto.setEmail("invalid-email");

        createAccount(createUserDto)
                .statusCode(400)
                .body("errors", containsString("Invalid email address"));

        createUserDto.setEmail("a@b.c");

        createAccount(createUserDto)
                .statusCode(400)
                .body("errors", containsString("Email must be between 5 and 100 characters"));

        String email = faker.lorem().characters(101);
        email = email + "@example.com";
        createUserDto.setEmail(email);

        createAccount(createUserDto)
                .statusCode(400)
                .body("errors", containsString("Email must be between 5 and 100 characters"));

    }

}
