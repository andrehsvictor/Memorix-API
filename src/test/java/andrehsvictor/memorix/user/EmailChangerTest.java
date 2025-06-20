package andrehsvictor.memorix.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

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
import andrehsvictor.memorix.common.exception.UnauthorizedException;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.common.util.FileUtil;
import andrehsvictor.memorix.user.dto.SendActionEmailDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailChanger Tests")
class EmailChangerTest {

    @Mock
    private UserService userService;

    @Mock
    private FileUtil fileUtil;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtService jwtService;

    @Mock
    private ActionTokenLifetimeProperties actionTokenLifetimeProperties;

    @InjectMocks
    private EmailChanger emailChanger;

    private User testUser;
    private SendActionEmailDto sendActionEmailDto;
    private Duration testLifetime;
    private UUID testUserId;
    private String newEmail;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        newEmail = "newemail@example.com";
        
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setEmail("current@example.com");
        
        sendActionEmailDto = new SendActionEmailDto();
        sendActionEmailDto.setEmail(newEmail);
        sendActionEmailDto.setUrl("https://example.com/change-email");
        sendActionEmailDto.setAction(EmailAction.CHANGE_EMAIL);

        testLifetime = Duration.ofHours(1);
    }

    @Test
    @DisplayName("Should send email change request successfully")
    void sendEmailChangeRequest_ShouldSendEmail_WhenUserAuthenticated() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(userService.getById(testUserId)).thenReturn(testUser);
        when(userService.existsByEmail(newEmail)).thenReturn(false);
        when(actionTokenLifetimeProperties.getChangeEmailLifetime()).thenReturn(testLifetime);
        when(fileUtil.processTemplate(eq("classpath:templates/change-email.html"), anyMap()))
                .thenReturn("<html>Email change content</html>");

        // When
        emailChanger.sendEmailChangeRequest(sendActionEmailDto);

        // Then
        verify(jwtService).getCurrentUserUuid();
        verify(userService).getById(testUserId);
        verify(userService).existsByEmail(newEmail);
        verify(actionTokenLifetimeProperties).getChangeEmailLifetime();
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmailChange()).isEqualTo(newEmail);
        assertThat(savedUser.getEmailChangeToken()).isNotNull();
        assertThat(savedUser.getEmailChangeTokenExpiresAt()).isAfter(LocalDateTime.now());
        
        verify(fileUtil).processTemplate(eq("classpath:templates/change-email.html"), anyMap());
        verify(emailService).send(
                eq(newEmail),
                eq("Confirm your email change - Memorix"),
                eq("<html>Email change content</html>"));
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when user is not authenticated")
    void sendEmailChangeRequest_ShouldThrowUnauthorizedException_WhenUserNotAuthenticated() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> emailChanger.sendEmailChangeRequest(sendActionEmailDto))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("User not authenticated");

        verify(userService, never()).getById(any(UUID.class));
        verify(userService, never()).existsByEmail(anyString());
        verify(userService, never()).save(any(User.class));
        verify(emailService, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when email is already in use")
    void sendEmailChangeRequest_ShouldThrowResourceConflictException_WhenEmailAlreadyInUse() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(userService.getById(testUserId)).thenReturn(testUser);
        when(userService.existsByEmail(newEmail)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> emailChanger.sendEmailChangeRequest(sendActionEmailDto))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessage("Email already in use: " + newEmail);

        verify(userService, never()).save(any(User.class));
        verify(emailService, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should generate URL with token parameter when URL has no query parameters")
    void sendEmailChangeRequest_ShouldAppendTokenWithQuestionMark_WhenUrlHasNoQueryParams() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(userService.getById(testUserId)).thenReturn(testUser);
        when(userService.existsByEmail(newEmail)).thenReturn(false);
        when(actionTokenLifetimeProperties.getChangeEmailLifetime()).thenReturn(testLifetime);
        when(fileUtil.processTemplate(eq("classpath:templates/change-email.html"), anyMap()))
                .thenReturn("<html>Content</html>");

        // When
        emailChanger.sendEmailChangeRequest(sendActionEmailDto);

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(fileUtil).processTemplate(eq("classpath:templates/change-email.html"), mapCaptor.capture());
        
        Map<String, String> templateParams = mapCaptor.getValue();
        String url = templateParams.get("url");
        assertThat(url).contains("?token=");
    }

    @Test
    @DisplayName("Should generate URL with token parameter when URL has existing query parameters")
    void sendEmailChangeRequest_ShouldAppendTokenWithAmpersand_WhenUrlHasQueryParams() {
        // Given
        sendActionEmailDto.setUrl("https://example.com/change-email?existing=param");
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(userService.getById(testUserId)).thenReturn(testUser);
        when(userService.existsByEmail(newEmail)).thenReturn(false);
        when(actionTokenLifetimeProperties.getChangeEmailLifetime()).thenReturn(testLifetime);
        when(fileUtil.processTemplate(eq("classpath:templates/change-email.html"), anyMap()))
                .thenReturn("<html>Content</html>");

        // When
        emailChanger.sendEmailChangeRequest(sendActionEmailDto);

        // Then
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, String>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(fileUtil).processTemplate(eq("classpath:templates/change-email.html"), mapCaptor.capture());
        
        Map<String, String> templateParams = mapCaptor.getValue();
        String url = templateParams.get("url");
        assertThat(url).contains("&token=");
    }

    @Test
    @DisplayName("Should change email successfully when token is valid and not expired")
    void changeEmail_ShouldUpdateEmail_WhenTokenValidAndNotExpired() {
        // Given
        String token = "valid-token";
        testUser.setEmailChange(newEmail);
        testUser.setEmailChangeToken(token);
        testUser.setEmailChangeTokenExpiresAt(LocalDateTime.now().plusHours(1));
        
        when(userService.getByEmailChangeToken(token)).thenReturn(testUser);
        when(userService.existsByEmail(newEmail)).thenReturn(false);

        // When
        emailChanger.changeEmail(token);

        // Then
        verify(userService).getByEmailChangeToken(token);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo(newEmail);
        assertThat(savedUser.isEmailVerified()).isTrue();
        assertThat(savedUser.getEmailChange()).isNull();
        assertThat(savedUser.getEmailChangeToken()).isNull();
        assertThat(savedUser.getEmailChangeTokenExpiresAt()).isNull();
    }

    @Test
    @DisplayName("Should throw GoneException when token is expired")
    void changeEmail_ShouldThrowGoneException_WhenTokenExpired() {
        // Given
        String token = "expired-token";
        testUser.setEmailChange(newEmail);
        testUser.setEmailChangeToken(token);
        testUser.setEmailChangeTokenExpiresAt(LocalDateTime.now().minusHours(1)); // Expired
        
        when(userService.getByEmailChangeToken(token)).thenReturn(testUser);

        // When & Then
        assertThatThrownBy(() -> emailChanger.changeEmail(token))
                .isInstanceOf(GoneException.class)
                .hasMessage("Email change token expired. Please request a new email change.");

        verify(userService, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when email is already taken during confirmation")
    void changeEmail_ShouldThrowResourceConflictException_WhenEmailAlreadyTaken() {
        // Given
        String token = "valid-token";
        testUser.setEmailChange(newEmail);
        testUser.setEmailChangeToken(token);
        testUser.setEmailChangeTokenExpiresAt(LocalDateTime.now().plusHours(1));
        
        when(userService.getByEmailChangeToken(token)).thenReturn(testUser);
        when(userService.existsByEmail(newEmail)).thenReturn(true); // Email taken since request

        // When & Then
        assertThatThrownBy(() -> emailChanger.changeEmail(token))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessage("Email already in use: " + newEmail);

        verify(userService, never()).save(any(User.class));
    }
}
