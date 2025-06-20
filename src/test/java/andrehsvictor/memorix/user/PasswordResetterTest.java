package andrehsvictor.memorix.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import andrehsvictor.memorix.common.email.EmailService;
import andrehsvictor.memorix.common.exception.GoneException;
import andrehsvictor.memorix.common.util.FileUtil;
import andrehsvictor.memorix.user.dto.SendActionEmailDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordResetter Tests")
class PasswordResetterTest {

    @Mock
    private UserService userService;

    @Mock
    private FileUtil fileUtil;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ActionTokenLifetimeProperties actionTokenLifetimeProperties;

    @InjectMocks
    private PasswordResetter passwordResetter;

    private User testUser;
    private SendActionEmailDto sendActionEmailDto;
    private Duration testLifetime;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusHours(1));

        sendActionEmailDto = new SendActionEmailDto();
        sendActionEmailDto.setEmail("test@example.com");
        sendActionEmailDto.setUrl("https://example.com/reset");
        sendActionEmailDto.setAction(EmailAction.RESET_PASSWORD);

        testLifetime = Duration.ofHours(1);
    }

    @Test
    @DisplayName("Should send password reset email successfully")
    void sendPasswordResetEmail_ShouldSendEmail_WhenUserExists() {
        // Given
        when(userService.getByEmail("test@example.com")).thenReturn(testUser);
        when(actionTokenLifetimeProperties.getResetPasswordLifetime()).thenReturn(testLifetime);
        when(fileUtil.processTemplate(eq("classpath:templates/reset-password.html"), anyMap()))
                .thenReturn("<html>Password reset email content</html>");

        // When
        passwordResetter.sendPasswordResetEmail(sendActionEmailDto);

        // Then
        verify(userService).getByEmail("test@example.com");
        verify(actionTokenLifetimeProperties).getResetPasswordLifetime();
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPasswordResetToken()).isNotNull();
        assertThat(savedUser.getPasswordResetTokenExpiresAt()).isAfter(LocalDateTime.now());
        
        verify(fileUtil).processTemplate(eq("classpath:templates/reset-password.html"), anyMap());
        verify(emailService).send(
                eq("test@example.com"),
                eq("Reset your password - Memorix"),
                eq("<html>Password reset email content</html>"));
    }

    @Test
    @DisplayName("Should generate URL with token parameter when URL has no query parameters")
    void sendPasswordResetEmail_ShouldAppendTokenWithQuestionMark_WhenUrlHasNoQueryParams() {
        // Given
        when(userService.getByEmail("test@example.com")).thenReturn(testUser);
        when(actionTokenLifetimeProperties.getResetPasswordLifetime()).thenReturn(testLifetime);
        when(fileUtil.processTemplate(eq("classpath:templates/reset-password.html"), anyMap()))
                .thenReturn("<html>Content</html>");

        // When
        passwordResetter.sendPasswordResetEmail(sendActionEmailDto);

        // Then
        verify(fileUtil).processTemplate(eq("classpath:templates/reset-password.html"), anyMap());
    }

    @Test
    @DisplayName("Should generate URL with token parameter when URL has existing query parameters")
    void sendPasswordResetEmail_ShouldAppendTokenWithAmpersand_WhenUrlHasQueryParams() {
        // Given
        sendActionEmailDto.setUrl("https://example.com/reset?existing=param");
        when(userService.getByEmail("test@example.com")).thenReturn(testUser);
        when(actionTokenLifetimeProperties.getResetPasswordLifetime()).thenReturn(testLifetime);
        when(fileUtil.processTemplate(eq("classpath:templates/reset-password.html"), anyMap()))
                .thenReturn("<html>Content</html>");

        // When
        passwordResetter.sendPasswordResetEmail(sendActionEmailDto);

        // Then
        verify(fileUtil).processTemplate(eq("classpath:templates/reset-password.html"), anyMap());
    }

    @Test
    @DisplayName("Should reset password successfully when token is valid and not expired")
    void resetPassword_ShouldResetPassword_WhenTokenValidAndNotExpired() {
        // Given
        String token = "valid-token";
        String newPassword = "newPassword123";
        testUser.setPasswordResetToken(token);
        testUser.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusHours(1));
        
        when(userService.getByPasswordResetToken(token)).thenReturn(testUser);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        // When
        passwordResetter.resetPassword(token, newPassword);

        // Then
        verify(userService).getByPasswordResetToken(token);
        verify(passwordEncoder).encode(newPassword);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isEqualTo("encodedNewPassword");
        assertThat(savedUser.getPasswordResetToken()).isNull();
        assertThat(savedUser.getPasswordResetTokenExpiresAt()).isNull();
    }

    @Test
    @DisplayName("Should throw GoneException when token is expired")
    void resetPassword_ShouldThrowGoneException_WhenTokenExpired() {
        // Given
        String token = "expired-token";
        String newPassword = "newPassword123";
        testUser.setPasswordResetToken(token);
        testUser.setPasswordResetTokenExpiresAt(LocalDateTime.now().minusHours(1)); // Expired
        
        when(userService.getByPasswordResetToken(token)).thenReturn(testUser);

        // When & Then
        assertThatThrownBy(() -> passwordResetter.resetPassword(token, newPassword))
                .isInstanceOf(GoneException.class)
                .hasMessage("Password reset token expired. Please request a new password reset.");

        verify(passwordEncoder).encode(newPassword);
        verify(userService).save(any());
    }
}
