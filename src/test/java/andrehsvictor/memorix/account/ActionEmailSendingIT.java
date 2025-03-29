package andrehsvictor.memorix.account;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;

import andrehsvictor.memorix.BaseIntegrationTest;
import andrehsvictor.memorix.account.dto.SendActionEmailDto;
import andrehsvictor.memorix.email.EmailService;
import andrehsvictor.memorix.user.User;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

@DisplayName("Action email sending integration test")
public class ActionEmailSendingIT extends BaseIntegrationTest {

    private static final String PATH = "/api/v1/account/send-action-email";

    @MockitoBean
    private EmailService emailService;

    private ValidatableResponse sendActionEmail(SendActionEmailDto sendActionEmailDto) {
        return given()
                .contentType(ContentType.JSON)
                .body(sendActionEmailDto)
                .post(PATH)
                .then();
    }

    @AfterEach
    void tearDown() {
        clearAll(User.class);
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @DisplayName("Should send a verification email")
    @Test
    void givenValidSendActionEmailDto_whenSendActionEmail_thenShouldSendVerificationEmail() {
        User user = createRandomUserInDb();
        SendActionEmailDto sendActionEmailDto = SendActionEmailDto.builder()
                .type("VERIFY_EMAIL")
                .email(user.getEmail())
                .redirectUrl("https://memorix.com/verify-email")
                .build();

        sendActionEmail(sendActionEmailDto)
                .statusCode(204)
                .body(is(emptyOrNullString()))
                .log()
                .all(true);

        verify(emailService).send(eq(user.getEmail()), eq("Verify your email"), anyString());

        Integer tokenCount = redisTemplate.keys("verification_token:*").size();
        assertThat(tokenCount).isEqualTo(1);
        String token = redisTemplate.keys("verification_token:*").iterator().next();
        Long userId = redisTemplate.opsForValue().get(token);
        assertThat(userId).isEqualTo(user.getId());
    }

    @DisplayName("Should send a password reset email")
    @Test
    void givenValidSendActionEmailDto_whenSendActionEmail_thenShouldSendPasswordResetEmail() {
        User user = createRandomUserInDb();
        SendActionEmailDto sendActionEmailDto = SendActionEmailDto.builder()
                .type("RESET_PASSWORD")
                .email(user.getEmail())
                .redirectUrl("https://memorix.com/reset-password")
                .build();

        sendActionEmail(sendActionEmailDto)
                .statusCode(204)
                .body(is(emptyOrNullString()))
                .log()
                .all(true);

        verify(emailService).send(eq(user.getEmail()), eq("Reset your password"), anyString());

        Integer tokenCount = redisTemplate.keys("reset_password_token:*").size();
        assertThat(tokenCount).isEqualTo(1);
        String token = redisTemplate.keys("reset_password_token:*").iterator().next();
        Long userId = redisTemplate.opsForValue().get(token);
        assertThat(userId).isEqualTo(user.getId());
    }

    @DisplayName("Should throw 400 when required fields are not provided or invalid")
    @Test
    void givenInvalidOrMissingFields_whenSendActionEmail_thenShouldThrow400() {
        SendActionEmailDto sendActionEmailDto = SendActionEmailDto.builder()
                .type(null)
                .email(null)
                .redirectUrl(null)
                .build();

        sendActionEmail(sendActionEmailDto)
                .statusCode(400)
                .body("errors", hasSize(3))
                .body("errors", containsInAnyOrder(
                        Map.of(
                                "field", "type",
                                "message", "Type is required"),
                        Map.of(
                                "field", "email",
                                "message", "Email is required"),
                        Map.of(
                                "field", "redirectUrl",
                                "message", "Redirect URL is required")));

        sendActionEmailDto = SendActionEmailDto.builder()
                .type(" ")
                .email("")
                .redirectUrl("")
                .build();

        sendActionEmail(sendActionEmailDto)
                .statusCode(400)
                .body("errors", hasSize(5))
                .body("errors", containsInAnyOrder(
                        Map.of(
                                "field", "type",
                                "message", "Type is required"),
                        Map.of(
                                "field", "type",
                                "message", "Type must be either RESET_PASSWORD or VERIFY_EMAIL"),
                        Map.of(
                                "field", "email",
                                "message", "Email is required"),
                        Map.of(
                                "field", "redirectUrl",
                                "message", "Redirect URL is invalid"),
                        Map.of(
                                "field", "redirectUrl",
                                "message", "Redirect URL is required")));

        sendActionEmailDto = SendActionEmailDto.builder()
                .type("INVALID_TYPE")
                .email("invalid_email")
                .redirectUrl("invalid_url")
                .build();

        sendActionEmail(sendActionEmailDto)
                .statusCode(400)
                .body("errors", hasSize(3))
                .body("errors", containsInAnyOrder(
                        Map.of(
                                "field", "type",
                                "message", "Type must be either RESET_PASSWORD or VERIFY_EMAIL"),
                        Map.of(
                                "field", "email",
                                "message", "Email is invalid"),
                        Map.of(
                                "field", "redirectUrl",
                                "message", "Redirect URL is invalid")));

    }

    @DisplayName("Should throw 409 when email is already verified")
    @Test
    void givenEmailAlreadyVerified_whenSendActionEmail_thenShouldThrow409() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        User user = transactionTemplate.execute(status -> {
            User newUser = createRandomUserInDb();
            newUser.setEmailVerified(true);
            entityManager.persist(newUser);
            entityManager.flush();
            entityManager.refresh(newUser);
            return newUser;
        });

        SendActionEmailDto sendActionEmailDto = SendActionEmailDto.builder()
                .type("VERIFY_EMAIL")
                .email(user.getEmail())
                .redirectUrl("https://memorix.com/verify-email")
                .build();
        sendActionEmail(sendActionEmailDto)
                .statusCode(409)
                .body("errors", hasSize(1))
                .body("errors", contains("Email already verified"));

    }

    @DisplayName("Should throw 404 when user is not found")
    @Test
    void givenUserNotFound_whenSendActionEmail_thenShouldThrow404() {
        SendActionEmailDto sendActionEmailDto = SendActionEmailDto.builder()
                .type("VERIFY_EMAIL")
                .email("nonexistent@example.com")
                .redirectUrl("https://memorix.com/verify-email")
                .build();

        sendActionEmail(sendActionEmailDto)
                .statusCode(404)
                .body("errors", hasSize(1))
                .body("errors", contains("User not found with email: " + sendActionEmailDto.getEmail()));
    }
}
