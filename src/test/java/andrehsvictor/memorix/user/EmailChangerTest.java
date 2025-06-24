package andrehsvictor.memorix.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import andrehsvictor.memorix.common.email.EmailService;
import andrehsvictor.memorix.common.exception.GoneException;
import andrehsvictor.memorix.common.exception.ResourceConflictException;
import andrehsvictor.memorix.common.util.FileUtil;
import andrehsvictor.memorix.user.dto.EmailChangeDto;

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
    private ActionTokenLifetimeProperties actionTokenLifetimeProperties;

    @InjectMocks
    private EmailChanger emailChanger;

    @Test
    @DisplayName("Should send email change request successfully")
    void shouldSendEmailChangeRequestSuccessfully() {
        // Arrange
        String newEmail = "new-email@example.com";
        String url = "https://example.com/change-email";
        UUID userId = UUID.randomUUID();
        
        EmailChangeDto dto = EmailChangeDto.builder()
                .email(newEmail)
                .url(url)
                .userId(userId)
                .build();
        
        User user = User.builder()
                .id(userId)
                .email("old-email@example.com")
                .build();
        
        Duration lifetime = Duration.ofMinutes(30);
        
        when(userService.getById(userId)).thenReturn(user);
        when(userService.existsByEmail(newEmail)).thenReturn(false);
        when(actionTokenLifetimeProperties.getChangeEmailLifetime()).thenReturn(lifetime);
        when(fileUtil.processTemplate(eq("classpath:templates/change-email.html"), any())).thenReturn("<html>Email content</html>");
        
        // Act
        emailChanger.sendEmailChangeRequest(dto);
        
        // Assert
        verify(userService).getById(userId);
        verify(userService).existsByEmail(newEmail);
        verify(actionTokenLifetimeProperties).getChangeEmailLifetime();
        verify(fileUtil).processTemplate(eq("classpath:templates/change-email.html"), any());
        verify(emailService).send(eq(newEmail), eq("Confirm your email change - Memorix"), anyString());
        verify(userService).save(user);
        
        assertEquals(newEmail, user.getEmailChange());
        assertNotNull(user.getEmailChangeToken());
        assertNotNull(user.getEmailChangeTokenExpiresAt());
    }
    
    @Test
    @DisplayName("Should throw ResourceConflictException when email is already in use during request")
    void shouldThrowResourceConflictExceptionWhenEmailIsAlreadyInUseOnRequest() {
        // Arrange
        String newEmail = "existing-email@example.com";
        String url = "https://example.com/change-email";
        UUID userId = UUID.randomUUID();
        
        EmailChangeDto dto = EmailChangeDto.builder()
                .email(newEmail)
                .url(url)
                .userId(userId)
                .build();
        
        User user = User.builder()
                .id(userId)
                .email("old-email@example.com")
                .build();
        
        when(userService.getById(userId)).thenReturn(user);
        when(userService.existsByEmail(newEmail)).thenReturn(true);
        
        // Act & Assert
        ResourceConflictException exception = assertThrows(
            ResourceConflictException.class,
            () -> emailChanger.sendEmailChangeRequest(dto)
        );
        
        assertEquals("Email already in use: " + newEmail, exception.getMessage());
        verify(userService, never()).save(any());
        verify(emailService, never()).send(anyString(), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should change email successfully")
    void shouldChangeEmailSuccessfully() {
        // Arrange
        String token = UUID.randomUUID().toString();
        String newEmail = "new-email@example.com";
        
        User user = User.builder()
                .email("old-email@example.com")
                .emailChange(newEmail)
                .emailChangeToken(token)
                .emailChangeTokenExpiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        
        when(userService.getByEmailChangeToken(token)).thenReturn(user);
        when(userService.existsByEmail(newEmail)).thenReturn(false);
        
        // Act
        emailChanger.changeEmail(token);
        
        // Assert
        verify(userService).getByEmailChangeToken(token);
        verify(userService).save(user);
        
        assertEquals(newEmail, user.getEmail());
        assertTrue(user.isEmailVerified());
        assertNull(user.getEmailChange());
        assertNull(user.getEmailChangeToken());
        assertNull(user.getEmailChangeTokenExpiresAt());
    }
    
    @Test
    @DisplayName("Should throw GoneException when token is expired")
    void shouldThrowGoneExceptionWhenTokenIsExpired() {
        // Arrange
        String token = UUID.randomUUID().toString();
        String newEmail = "new-email@example.com";
        
        User user = User.builder()
                .email("old-email@example.com")
                .emailChange(newEmail)
                .emailChangeToken(token)
                .emailChangeTokenExpiresAt(LocalDateTime.now().minusMinutes(10)) // expired token
                .build();
        
        when(userService.getByEmailChangeToken(token)).thenReturn(user);
        
        // Act & Assert
        GoneException exception = assertThrows(
            GoneException.class,
            () -> emailChanger.changeEmail(token)
        );
        
        assertEquals("Email change token expired. Please request a new email change.", exception.getMessage());
        verify(userService, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw ResourceConflictException when email is already in use during change")
    void shouldThrowResourceConflictExceptionWhenEmailIsAlreadyInUseOnChange() {
        // Arrange
        String token = UUID.randomUUID().toString();
        String newEmail = "existing-email@example.com";
        
        User user = User.builder()
                .email("old-email@example.com")
                .emailChange(newEmail)
                .emailChangeToken(token)
                .emailChangeTokenExpiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        
        when(userService.getByEmailChangeToken(token)).thenReturn(user);
        when(userService.existsByEmail(newEmail)).thenReturn(true);
        
        // Act & Assert
        ResourceConflictException exception = assertThrows(
            ResourceConflictException.class,
            () -> emailChanger.changeEmail(token)
        );
        
        assertEquals("Email already in use: " + newEmail, exception.getMessage());
        verify(userService, never()).save(any());
    }
    
    @Test
    @DisplayName("Should format duration correctly")
    void shouldFormatDurationCorrectly() {
        // Using reflection to access private method
        try {
            java.lang.reflect.Method method = EmailChanger.class.getDeclaredMethod("formatDuration", Duration.class);
            method.setAccessible(true);
            
            // Test hours
            assertEquals("2 hours", method.invoke(emailChanger, Duration.ofHours(2)));
            assertEquals("1 hour", method.invoke(emailChanger, Duration.ofHours(1)));
            
            // Test minutes
            assertEquals("30 minutes", method.invoke(emailChanger, Duration.ofMinutes(30)));
            assertEquals("1 minute", method.invoke(emailChanger, Duration.ofMinutes(1)));
            
        } catch (Exception e) {
            fail("Failed to test private formatDuration method: " + e.getMessage());
        }
    }
}
