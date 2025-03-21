package andrehsvictor.memorix.account;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import andrehsvictor.memorix.IntegrationTest;
import andrehsvictor.memorix.user.dto.CreateUserDto;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import net.datafaker.Faker;

@DisplayName("Account Creation Integration Test")
class AccountCreationIT extends IntegrationTest {

    private static final String PATH = "/api/v1/account";

    private Faker faker = new Faker();

    private CreateUserDto minimalCreateUserDto() {
        return CreateUserDto.builder()
                .username(faker.internet().username())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .displayName(faker.name().fullName())
                .build();
    }

    private CreateUserDto createUserDtoWithBioAndPictureUrl() {
        return CreateUserDto.builder()
                .username(faker.internet().username())
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .displayName(faker.name().fullName())
                .bio(faker.lorem().sentence())
                .pictureUrl(faker.internet().image())
                .build();
    }

    private ValidatableResponse createAccount(CreateUserDto createUserDto) {
        return given()
                .contentType(ContentType.JSON)
                .body(createUserDto)
                .when()
                .post(PATH)
                .then();
    }

    @Test
    @DisplayName("Should create account when given minimal user data")
    void givenMinimalCreateUserDto_whenCreate_thenAccountCreated() {
        CreateUserDto createUserDto = minimalCreateUserDto();

        createAccount(createUserDto)
                .statusCode(201)
                .body("username", equalTo(createUserDto.getUsername()))
                .body("email", equalTo(createUserDto.getEmail()))
                .body("displayName", equalTo(createUserDto.getDisplayName()))
                .body("bio", equalTo(null))
                .body("pictureUrl", equalTo(null))
                .body("createdAt", notNullValue(LocalDateTime.class))
                .body("updatedAt", notNullValue(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should create account when given user data with bio and picture URL")
    void givenCreateUserDtoWithBioAndPictureUrl_whenCreate_thenAccountCreated() {
        CreateUserDto createUserDto = createUserDtoWithBioAndPictureUrl();

        createAccount(createUserDto)
                .statusCode(201)
                .body("username", equalTo(createUserDto.getUsername()))
                .body("email", equalTo(createUserDto.getEmail()))
                .body("displayName", equalTo(createUserDto.getDisplayName()))
                .body("bio", equalTo(createUserDto.getBio()))
                .body("pictureUrl", equalTo(createUserDto.getPictureUrl()))
                .body("createdAt", notNullValue(LocalDateTime.class))
                .body("updatedAt", notNullValue(LocalDateTime.class));
    }

}
