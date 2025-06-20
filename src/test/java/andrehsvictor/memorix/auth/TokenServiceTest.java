package andrehsvictor.memorix.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import andrehsvictor.memorix.auth.dto.CredentialsDto;
import andrehsvictor.memorix.auth.dto.IdTokenDto;
import andrehsvictor.memorix.auth.dto.RefreshTokenDto;
import andrehsvictor.memorix.auth.dto.RevokeTokenDto;
import andrehsvictor.memorix.auth.dto.TokenDto;
import andrehsvictor.memorix.common.google.GoogleAuthenticationService;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.common.revokedtoken.RevokedTokenService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
@DisplayName("TokenService Tests")
class TokenServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private RevokedTokenService revokedTokenService;

    @Mock
    private GoogleAuthenticationService googleAuthenticationService;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt accessToken;

    @Mock
    private Jwt refreshToken;

    @InjectMocks
    private TokenService tokenService;

    private String testSubject;
    private CredentialsDto credentialsDto;
    private IdTokenDto idTokenDto;
    private RefreshTokenDto refreshTokenDto;
    private RevokeTokenDto revokeTokenDto;

    @BeforeEach
    void setUp() {
        testSubject = "test-user-id";
        credentialsDto = CredentialsDto.builder()
                .username("testuser")
                .password("testpassword")
                .build();

        idTokenDto = new IdTokenDto();
        idTokenDto.setIdToken("test-id-token");

        refreshTokenDto = RefreshTokenDto.builder()
                .refreshToken("test-refresh-token")
                .build();

        revokeTokenDto = RevokeTokenDto.builder()
                .token("test-token")
                .build();

        when(accessToken.getTokenValue()).thenReturn("access-token-value");
        when(accessToken.getIssuedAt()).thenReturn(Instant.now());
        when(accessToken.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));

        when(refreshToken.getTokenValue()).thenReturn("refresh-token-value");
        when(refreshToken.getSubject()).thenReturn(testSubject);
    }

    @Test
    @DisplayName("Should request token successfully with valid credentials")
    void request_ShouldReturnTokenDto_WhenCredentialsValid() {
        // Given
        when(authentication.getName()).thenReturn(testSubject);
        when(authenticationService.authenticate(credentialsDto.getUsername(), credentialsDto.getPassword()))
                .thenReturn(authentication);
        when(jwtService.issueAccessToken(testSubject)).thenReturn(accessToken);
        when(jwtService.issueRefreshToken(testSubject)).thenReturn(refreshToken);

        // When
        TokenDto result = tokenService.request(credentialsDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("access-token-value");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token-value");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getExpiresIn()).isEqualTo(3600L);

        verify(authenticationService).authenticate(credentialsDto.getUsername(), credentialsDto.getPassword());
        verify(jwtService).issueAccessToken(testSubject);
        verify(jwtService).issueRefreshToken(testSubject);
    }

    @Test
    @DisplayName("Should authenticate with Google successfully")
    void google_ShouldReturnTokenDto_WhenIdTokenValid() {
        // Given
        when(authentication.getName()).thenReturn(testSubject);
        when(googleAuthenticationService.authenticate(idTokenDto.getIdToken())).thenReturn(authentication);
        when(jwtService.issueAccessToken(testSubject)).thenReturn(accessToken);
        when(jwtService.issueRefreshToken(testSubject)).thenReturn(refreshToken);

        // When
        TokenDto result = tokenService.google(idTokenDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("access-token-value");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token-value");
        assertThat(result.getTokenType()).isEqualTo("Bearer");

        verify(googleAuthenticationService).authenticate(idTokenDto.getIdToken());
        verify(jwtService).issueAccessToken(testSubject);
        verify(jwtService).issueRefreshToken(testSubject);
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void refresh_ShouldReturnNewTokenDto_WhenRefreshTokenValid() {
        // Given
        when(jwtService.decode(refreshTokenDto.getRefreshToken())).thenReturn(refreshToken);
        when(jwtService.issueAccessToken(testSubject)).thenReturn(accessToken);
        when(jwtService.issueRefreshToken(testSubject)).thenReturn(refreshToken);

        // When
        TokenDto result = tokenService.refresh(refreshTokenDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("access-token-value");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token-value");

        verify(jwtService).decode(refreshTokenDto.getRefreshToken());
        verify(jwtService).validateRefreshToken(refreshToken);
        verify(revokedTokenService).revoke(refreshToken);
        verify(jwtService).issueAccessToken(testSubject);
        verify(jwtService).issueRefreshToken(testSubject);
    }

    @Test
    @DisplayName("Should revoke token successfully")
    void revoke_ShouldRevokeToken() {
        // Given
        Jwt jwt = refreshToken;
        when(jwtService.decode(revokeTokenDto.getToken())).thenReturn(jwt);

        // When
        tokenService.revoke(revokeTokenDto);

        // Then
        verify(jwtService).decode(revokeTokenDto.getToken());
        verify(revokedTokenService).revoke(jwt);
    }
}
