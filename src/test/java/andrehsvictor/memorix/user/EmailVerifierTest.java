package andrehsvictor.memorix.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import andrehsvictor.memorix.common.email.EmailService;
import andrehsvictor.memorix.common.exception.GoneException;
import andrehsvictor.memorix.common.exception.ResourceConflictException;
import andrehsvictor.memorix.common.util.FileUtil;
import andrehsvictor.memorix.user.dto.SendActionEmailDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailVerifier Tests")
class EmailVerifierTest {

    @Mock
    private UserService userService;

    @Mock
    private FileUtil fileUtil;

    @Mock
    private EmailService emailService;

    @Mock
    private ActionTokenLifetimeProperties actionTokenLifetimeProperties;

    @InjectMocks
    private EmailVerifier emailVerifier;

    private User testUser;
    private SendActionEmailDto sendActionEmailDto;
    private Duration testLifetime;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setEmailVerified(false);
        testUser.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(1));

        sendActionEmailDto = new SendActionEmailDto();
        sendActionEmailDto.setEmail("test@example.com");
        sendActionEmailDto.setUrl("https://example.com/verify");
        sendActionEmailDto.setAction(EmailAction.VERIFY_EMAIL);

        testLifetime = Duration.ofHours(24);
    }

    @Test
    @DisplayName("Should send verification email successfully when user is not verified")
    void sendVerificationEmail_ShouldSendEmail_WhenUserNotVerified() {
        // Given
        when(userService.getByEmail("test@example.com")).thenReturn(testUser);
        when(actionTokenLifetimeProperties.getVerifyEmailLifetime()).thenReturn(testLifetime);
        when(fileUtil.processTemplate(eq("classpath:templates/verify-email.html"), anyMap()))
                .thenReturn("<html>Verification email content</html>");

        // When
        emailVerifier.sendVerificationEmail(sendActionEmailDto);

        // Then
        verify(userService).getByEmail("test@example.com");
        verify(actionTokenLifetimeProperties).getVerifyEmailLifetime();
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmailVerificationToken()).isNotNull();
        assertThat(savedUser.getEmailVerificationTokenExpiresAt()).isAfter(LocalDateTime.now());
        
        verify(fileUtil).processTemplate(eq("classpath:templates/verify-email.html"), anyMap());
        verify(emailService).send(
                eq("test@example.com"),
                eq("Verify your email address - Memorix"),
                eq("<html>Verification email content</html>"));
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when email already verified")
    void sendVerificationEmail_ShouldThrowException_WhenEmailAlreadyVerified() {
        // Given
        testUser.setEmailVerified(true);
        when(userService.getByEmail("test@example.com")).thenReturn(testUser);

        // When & Then
        assertThatThrownBy(() -> emailVerifier.sendVerificationEmail(sendActionEmailDto))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessage("Email already verified: test@example.com");

        verify(userService, never()).save(any());
        verify(emailService, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should verify email successfully when token is valid and not expired")
    void verifyEmail_ShouldVerifyEmail_WhenTokenValidAndNotExpired() {
        // Given
        String token = "valid-token";
        testUser.setEmailVerificationToken(token);
        testUser.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(1));
        testUser.setEmailVerified(false);
        
        when(userService.getByEmailVerificationToken(token)).thenReturn(testUser);

        // When
        emailVerifier.verifyEmail(token);

        // Then
        verify(userService).getByEmailVerificationToken(token);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.isEmailVerified()).isTrue();
        assertThat(savedUser.getEmailVerificationToken()).isNull();
        assertThat(savedUser.getEmailVerificationTokenExpiresAt()).isNull();
    }

    @Test
    @DisplayName("Should throw GoneException when token is expired")
    void verifyEmail_ShouldThrowGoneException_WhenTokenExpired() {
        // Given
        String token = "expired-token";
        testUser.setEmailVerificationToken(token);
        testUser.setEmailVerificationTokenExpiresAt(LocalDateTime.now().minusHours(1)); // Expired
        
        when(userService.getByEmailVerificationToken(token)).thenReturn(testUser);

        // When & Then
        assertThatThrownBy(() -> emailVerifier.verifyEmail(token))
                .isInstanceOf(GoneException.class)
                .hasMessage("Action token expired. Please request a new verification email.");

        verify(userService, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when email already verified")
    void verifyEmail_ShouldThrowException_WhenEmailAlreadyVerified() {
        // Given
        String token = "valid-token";
        testUser.setEmailVerificationToken(token);
        testUser.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(1));
        testUser.setEmailVerified(true); // Already verified
        
        when(userService.getByEmailVerificationToken(token)).thenReturn(testUser);

        // When & Then
        assertThatThrownBy(() -> emailVerifier.verifyEmail(token))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessage("Email already verified: " + testUser.getEmail());

        verify(userService, never()).save(any());
    }
}
