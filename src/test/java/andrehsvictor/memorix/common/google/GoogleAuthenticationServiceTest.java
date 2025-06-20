package andrehsvictor.memorix.common.google;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.GeneralSecurityException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import andrehsvictor.memorix.common.exception.BadRequestException;
import andrehsvictor.memorix.common.exception.ResourceConflictException;
import andrehsvictor.memorix.common.exception.ResourceNotFoundException;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserProvider;
import andrehsvictor.memorix.user.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("GoogleAuthenticationService Tests")
class GoogleAuthenticationServiceTest {

    @Mock
    private GoogleIdTokenVerifier tokenVerifier;

    @Mock
    private UserService userService;

    @Mock
    private GoogleIdToken googleIdToken;

    @Mock
    private GoogleIdToken.Payload payload;

    @InjectMocks
    private GoogleAuthenticationService googleAuthenticationService;

    private String testIdToken;
    private String testEmail;
    private String testProviderId;
    private User testUser;

    @BeforeEach
    void setUp() {
        testIdToken = "test-id-token";
        testEmail = "test@example.com";
        testProviderId = "google-123456";

        testUser = User.builder()
                .email(testEmail)
                .username("testuser")
                .provider(UserProvider.GOOGLE)
                .providerId(testProviderId)
                .emailVerified(true)
                .build();
    }

    @Test
    @DisplayName("Should authenticate existing user successfully")
    void authenticate_ShouldReturnAuthentication_WhenUserExists() throws Exception {
        // Given
        when(tokenVerifier.verify(testIdToken)).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn(testEmail);
        when(payload.getSubject()).thenReturn(testProviderId);
        when(payload.getEmailVerified()).thenReturn(true);
        when(userService.getByProviderId(testProviderId)).thenReturn(testUser);

        // When
        Authentication result = googleAuthenticationService.authenticate(testIdToken);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isAuthenticated()).isTrue();
        verify(tokenVerifier).verify(testIdToken);
        verify(userService).getByProviderId(testProviderId);
    }

    @Test
    @DisplayName("Should create new user when user does not exist")
    void authenticate_ShouldCreateUser_WhenUserDoesNotExist() throws Exception {
        // Given
        when(tokenVerifier.verify(testIdToken)).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn(testEmail);
        when(payload.getSubject()).thenReturn(testProviderId);
        when(payload.getEmailVerified()).thenReturn(true);
        when(payload.get("name")).thenReturn("Test User");
        when(payload.get("picture")).thenReturn("https://example.com/picture.jpg");
        
        when(userService.getByProviderId(testProviderId)).thenThrow(new ResourceNotFoundException("User not found"));
        when(userService.getByEmail(testEmail)).thenThrow(new ResourceNotFoundException("User not found"));
        when(userService.existsByUsername(anyString())).thenReturn(false);
        when(userService.save(any(User.class))).thenReturn(testUser);

        // When
        Authentication result = googleAuthenticationService.authenticate(testIdToken);

        // Then
        assertThat(result).isNotNull();
        verify(userService).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when token verification fails")
    void authenticate_ShouldThrowBadRequestException_WhenTokenInvalid() throws Exception {
        // Given
        when(tokenVerifier.verify(testIdToken)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> googleAuthenticationService.authenticate(testIdToken))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Invalid ID token");

        verify(userService, never()).getByProviderId(anyString());
    }

    @Test
    @DisplayName("Should throw RuntimeException when token verification throws exception")
    void authenticate_ShouldThrowRuntimeException_WhenVerificationThrowsException() throws Exception {
        // Given
        when(tokenVerifier.verify(testIdToken)).thenThrow(new GeneralSecurityException("Security error"));

        // When & Then
        assertThatThrownBy(() -> googleAuthenticationService.authenticate(testIdToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to verify ID token");
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when provider ID has different email")
    void authenticate_ShouldThrowException_WhenProviderIdHasDifferentEmail() throws Exception {
        // Given
        testUser.setEmail("different@example.com");
        when(tokenVerifier.verify(testIdToken)).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn(testEmail);
        when(payload.getSubject()).thenReturn(testProviderId);
        when(userService.getByProviderId(testProviderId)).thenReturn(testUser);

        // When & Then
        assertThatThrownBy(() -> googleAuthenticationService.authenticate(testIdToken))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("has a different email address");
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when email exists with different provider")
    void authenticate_ShouldThrowException_WhenEmailExistsWithDifferentProvider() throws Exception {
        // Given
        testUser.setProvider(UserProvider.LOCAL);
        when(tokenVerifier.verify(testIdToken)).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn(testEmail);
        when(payload.getSubject()).thenReturn(testProviderId);
        when(userService.getByProviderId(testProviderId)).thenThrow(new ResourceNotFoundException("User not found"));
        when(userService.getByEmail(testEmail)).thenReturn(testUser);

        // When & Then
        assertThatThrownBy(() -> googleAuthenticationService.authenticate(testIdToken))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("is already registered with a different provider");
    }

    @Test
    @DisplayName("Should update user with provider ID when missing")
    void authenticate_ShouldUpdateUserWithProviderId_WhenProviderIdMissing() throws Exception {
        // Given
        testUser.setProviderId(null);
        when(tokenVerifier.verify(testIdToken)).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn(testEmail);
        when(payload.getSubject()).thenReturn(testProviderId);
        when(payload.getEmailVerified()).thenReturn(true);
        when(userService.getByProviderId(testProviderId)).thenThrow(new ResourceNotFoundException("User not found"));
        when(userService.getByEmail(testEmail)).thenReturn(testUser);
        when(userService.save(testUser)).thenReturn(testUser);

        // When
        googleAuthenticationService.authenticate(testIdToken);

        // Then
        verify(userService).save(testUser);
        assertThat(testUser.getProviderId()).isEqualTo(testProviderId);
    }

    @Test
    @DisplayName("Should generate unique username when base username exists")
    void authenticate_ShouldGenerateUniqueUsername_WhenBaseUsernameExists() throws Exception {
        // Given
        when(tokenVerifier.verify(testIdToken)).thenReturn(googleIdToken);
        when(googleIdToken.getPayload()).thenReturn(payload);
        when(payload.getEmail()).thenReturn(testEmail);
        when(payload.getSubject()).thenReturn(testProviderId);
        when(payload.getEmailVerified()).thenReturn(true);
        when(payload.get("name")).thenReturn("Test User");
        when(payload.get("picture")).thenReturn("https://example.com/picture.jpg");
        
        when(userService.getByProviderId(testProviderId)).thenThrow(new ResourceNotFoundException("User not found"));
        when(userService.getByEmail(testEmail)).thenThrow(new ResourceNotFoundException("User not found"));
        
        // Mock specific behavior for existsByUsername
        // First call with exact "test" baseUsername returns true (username exists)
        when(userService.existsByUsername(eq("test"))).thenReturn(true);
        
        // Any other calls with a string that's not "test" will return false (generated username doesn't exist)
        // This is more specific than the previous anyString() matcher
        when(userService.existsByUsername(argThat(arg -> arg != null && !arg.equals("test")))).thenReturn(false);
        
        when(userService.save(any(User.class))).thenReturn(testUser);

        // When
        googleAuthenticationService.authenticate(testIdToken);

        // Then
        verify(userService).save(any(User.class));
        // Verify that existsByUsername was called with "test" 
        verify(userService).existsByUsername(eq("test"));
        // Verify that existsByUsername was called at least once with a non-"test" value
        verify(userService).existsByUsername(argThat(arg -> arg != null && !arg.equals("test")));
    }
}
