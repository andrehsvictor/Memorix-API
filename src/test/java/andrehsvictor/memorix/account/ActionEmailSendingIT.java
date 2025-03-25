package andrehsvictor.memorix.account;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import andrehsvictor.memorix.BaseIntegrationTest;
import andrehsvictor.memorix.account.dto.SendActionEmailDto;
import andrehsvictor.memorix.email.EmailService;
import andrehsvictor.memorix.user.User;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

@DisplayName("Action email sending integration test")
public class ActionEmailSendingIT extends BaseIntegrationTest {

    private static final String PATH = "/api/v1/account/send-action-email";

    @Autowired
    @MockitoSpyBean
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

}
