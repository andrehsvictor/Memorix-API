package andrehsvictor.memorix.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService Tests")
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;

    private String testUsername;
    private String testPassword;
    private UsernamePasswordAuthenticationToken authToken;

    @BeforeEach
    void setUp() {
        testUsername = "testuser";
        testPassword = "testpassword";
        authToken = new UsernamePasswordAuthenticationToken(testUsername, testPassword);
    }

    @Test
    @DisplayName("Should authenticate successfully with valid credentials")
    void authenticate_ShouldReturnAuthentication_WhenCredentialsValid() {
        // Given
        when(authenticationManager.authenticate(authToken)).thenReturn(authentication);

        // When
        Authentication result = authenticationService.authenticate(testUsername, testPassword);

        // Then
        assertThat(result).isEqualTo(authentication);
        verify(authenticationManager).authenticate(authToken);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when credentials are invalid")
    void authenticate_ShouldThrowBadCredentialsException_WhenCredentialsInvalid() {
        // Given
        when(authenticationManager.authenticate(authToken))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThatThrownBy(() -> authenticationService.authenticate(testUsername, testPassword))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid credentials");

        verify(authenticationManager).authenticate(authToken);
    }

    @Test
    @DisplayName("Should throw DisabledException when user account is disabled")
    void authenticate_ShouldThrowDisabledException_WhenAccountDisabled() {
        // Given
        when(authenticationManager.authenticate(authToken))
                .thenThrow(new DisabledException("Account disabled"));

        // When & Then
        assertThatThrownBy(() -> authenticationService.authenticate(testUsername, testPassword))
                .isInstanceOf(DisabledException.class)
                .hasMessageContaining("User must verify their email before logging in");

        verify(authenticationManager).authenticate(authToken);
    }

    @Test
    @DisplayName("Should throw RuntimeException for other authentication failures")
    void authenticate_ShouldThrowRuntimeException_WhenOtherAuthenticationFailure() {
        // Given
        when(authenticationManager.authenticate(authToken))
                .thenThrow(new RuntimeException("Some other error"));

        // When & Then
        assertThatThrownBy(() -> authenticationService.authenticate(testUsername, testPassword))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Authentication failed");

        verify(authenticationManager).authenticate(authToken);
    }
}
