package andrehsvictor.memorix.user.it;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import andrehsvictor.memorix.AbstractIntegrationTest;
import andrehsvictor.memorix.user.EmailAction;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserRepository;
import andrehsvictor.memorix.user.UserRole;
import io.restassured.http.ContentType;

@DisplayName("User Verification Integration Tests")
public class UserVerificationIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private User unverifiedUser;
    private final String password = "Password123!";
    private final String mockToken = "valid-token-for-testing";
    private String accessToken;

    @BeforeEach
    void setUp() {
        // Clear MailHog inbox before each test
        String mailhogUrl = getMailhogUrl() + "/api/v1/messages";
        given()
                .delete(mailhogUrl)
                .then()
                .statusCode(HttpStatus.OK.value());
        userRepository.deleteAll();

        // Create verified user with a mock password
        testUser = User.builder()
                .email("test@example.com")
                .username("testuser")
                .displayName("Test User")
                .password(passwordEncoder.encode(password))
                .role(UserRole.USER)
                .emailVerified(true)
                .build();
        testUser = userRepository.save(testUser);

        accessToken = given()
                .contentType(ContentType.JSON)
                .body(Map.of("username", testUser.getUsername(), "password", password))
                .when()
                .post("/api/v1/auth/token")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("accessToken");

        // Create unverified user with a mock verification token
        unverifiedUser = User.builder()
                .email("unverified@example.com")
                .username("unverified")
                .displayName("Unverified User")
                .password(passwordEncoder.encode(password))
                .role(UserRole.USER)
                .emailVerified(false)
                .emailVerificationToken(mockToken)
                .emailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24))
                .build();
        unverifiedUser = userRepository.save(unverifiedUser);
    }

    @Test
    @DisplayName("Should send verification email to user")
    void sendActionEmail_ShouldReturn204_WhenRequestingVerification() throws InterruptedException {
        Map<String, Object> dto = new HashMap<>();
        dto.put("email", unverifiedUser.getEmail());
        dto.put("action", EmailAction.VERIFY_EMAIL);
        dto.put("url", "http://localhost:3000/verify-email");

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/v1/users/send-action-email")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Thread.sleep(500);

        String mailhogUrl = getMailhogUrl() + "/api/v2/messages";
        given()
                .get(mailhogUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("total", equalTo(1))
                .body("items[0].From.Mailbox", equalTo("noreply"))
                .body("items[0].From.Domain", equalTo("memorix.io"))
                .body("items[0].To[0].Mailbox", equalTo("unverified"))
                .body("items[0].To[0].Domain", equalTo("example.com"))
                .body("items[0].Content.Headers.Subject[0]", equalTo("Verify your email address - Memorix"))
                .body("items[0].Content.Body", containsString(
                        "Thank you for registering to Memorix. To complete your registration, please verify your email address by clicking the link below:"))
                .body("items[0].Content.Body", containsString("Verify Email"))
                .body("items[0].Content.Body", containsString("This link will expire in 15 minutes."))
                .body("items[0].Content.Body",
                        containsString("If you did not register for an account, please ignore this email."))
                .body("items[0].Content.Body", containsString("This is an automated message, please do not reply."));
    }

    @Test
    @DisplayName("Should send password reset email to user")
    void sendActionEmail_ShouldReturn204_WhenRequestingPasswordReset() throws InterruptedException {
        Map<String, Object> dto = new HashMap<>();
        dto.put("email", testUser.getEmail());
        dto.put("action", EmailAction.RESET_PASSWORD);
        dto.put("url", "http://localhost:3000/reset-password");

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/v1/users/send-action-email")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Thread.sleep(500);

        String mailhogUrl = getMailhogUrl() + "/api/v2/messages";
        given()
                .get(mailhogUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("total", equalTo(1))
                .body("items[0].From.Mailbox", equalTo("noreply"))
                .body("items[0].From.Domain", equalTo("memorix.io"))
                .body("items[0].To[0].Mailbox", equalTo("test"))
                .body("items[0].To[0].Domain", equalTo("example.com"))
                .body("items[0].Content.Headers.Subject[0]", equalTo("Reset your password - Memorix"));
    }

    @Test
    @DisplayName("Should send email change request email to user")
    void sendEmailChangeVerificationEmail_ShouldReturn204_WhenRequestingEmailChange() throws InterruptedException {
        Map<String, Object> dto = new HashMap<>();
        dto.put("new", "newemail@memorix.io");
        dto.put("url", "http://localhost:3000/change-email");

        given()
                .contentType(ContentType.JSON)
                .headers("Authorization", "Bearer " + accessToken)
                .body(dto)
                .when()
                .post("/api/v1/users/me/send-email-change-verification")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Thread.sleep(500);

        String mailhogUrl = getMailhogUrl() + "/api/v2/messages";
        given()
                .get(mailhogUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("total", equalTo(1))
                .body("items[0].From.Mailbox", equalTo("noreply"))
                .body("items[0].From.Domain", equalTo("memorix.io"))
                .body("items[0].To[0].Mailbox", equalTo("newemail"))
                .body("items[0].To[0].Domain", equalTo("memorix.io"))
                .body("items[0].Content.Headers.Subject[0]", equalTo("Confirm your email change - Memorix"));
    }

    @Test
    @DisplayName("Should verify user email with valid token")
    void verifyEmail_ShouldVerifyEmail_WhenTokenIsValid() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("token", mockToken);

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/v1/users/verify-email")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Verificar se o email foi marcado como verificado
        User updatedUser = userRepository.findById(unverifiedUser.getId()).orElseThrow();
        assertThat(updatedUser.isEmailVerified()).isTrue();
    }

    @Test
    @DisplayName("Should return 400 when token for email verification is invalid")
    void verifyEmail_ShouldReturn400_WhenTokenIsInvalid() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("token", "invalid-token");

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/v1/users/verify-email")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should change user email with valid token")
    void changeEmail_ShouldChangeEmail_WhenTokenIsValid() {
        // Configurar token de mudança de email para o usuário de teste
        String newEmail = "newemail@example.com";
        testUser.setEmailChangeToken(mockToken);
        testUser.setEmailChangeTokenExpiresAt(LocalDateTime.now().plusHours(24));
        testUser.setEmailChange(newEmail);
        userRepository.save(testUser);

        Map<String, Object> dto = new HashMap<>();
        dto.put("token", mockToken);

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .put("/api/v1/users/email")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Verificar se o email foi alterado
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
    }

    @Test
    @DisplayName("Should reset password with valid token")
    void resetPassword_ShouldResetPassword_WhenTokenIsValid() {
        // Configurar token de redefinição de senha para o usuário de teste
        String newPassword = "NewPassword123!";
        testUser.setPasswordResetToken(mockToken);
        testUser.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusHours(24));
        userRepository.save(testUser);

        Map<String, Object> dto = new HashMap<>();
        dto.put("token", mockToken);
        dto.put("password", newPassword);

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .post("/api/v1/users/reset-password")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Verificar se a senha foi alterada
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(passwordEncoder.matches(newPassword, updatedUser.getPassword())).isTrue();
    }
}
