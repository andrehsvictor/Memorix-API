package andrehsvictor.memorix.common.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import andrehsvictor.memorix.common.exception.BadRequestException;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Tests")
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private JwtLifetimeProperties jwtLifetimeProperties;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private JwtAuthenticationToken jwtAuthenticationToken;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private JwtService jwtService;

    private String testSubject;
    private String testTokenValue;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testSubject = UUID.randomUUID().toString();
        testUserId = UUID.fromString(testSubject);
        testTokenValue = "test.jwt.token";

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Should get current user UUID from JWT token")
    void getCurrentUserUuid_ShouldReturnUuid_WhenJwtAuthenticationTokenPresent() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn(testSubject);

        // When
        UUID result = jwtService.getCurrentUserUuid();

        // Then
        assertThat(result).isEqualTo(testUserId);
        verify(securityContext).getAuthentication();
        verify(jwtAuthenticationToken).getToken();
        verify(jwt).getSubject();
    }

    @Test
    @DisplayName("Should return null when authentication is not JWT type")
    void getCurrentUserUuid_ShouldReturnNull_WhenNotJwtAuthenticationToken() {
        // Given
        Authentication nonJwtAuth = org.mockito.Mockito.mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(nonJwtAuth);

        // When
        UUID result = jwtService.getCurrentUserUuid();

        // Then
        assertThat(result).isNull();
        verify(securityContext).getAuthentication();
    }

    @Test
    @DisplayName("Should issue access token successfully")
    void issueAccessToken_ShouldReturnJwt() {
        // Given
        Duration accessTokenLifetime = Duration.ofHours(1);
        when(jwtLifetimeProperties.getAccessTokenLifetime()).thenReturn(accessTokenLifetime);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // When
        Jwt result = jwtService.issueAccessToken(testSubject);

        // Then
        assertThat(result).isEqualTo(jwt);
        verify(jwtLifetimeProperties).getAccessTokenLifetime();
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    @DisplayName("Should issue refresh token successfully")
    void issueRefreshToken_ShouldReturnJwt() {
        // Given
        Duration refreshTokenLifetime = Duration.ofDays(7);
        when(jwtLifetimeProperties.getRefreshTokenLifetime()).thenReturn(refreshTokenLifetime);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // When
        Jwt result = jwtService.issueRefreshToken(testSubject);

        // Then
        assertThat(result).isEqualTo(jwt);
        verify(jwtLifetimeProperties).getRefreshTokenLifetime();
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    @DisplayName("Should decode token successfully")
    void decode_ShouldReturnJwt_WhenTokenValid() {
        // Given
        when(jwtDecoder.decode(testTokenValue)).thenReturn(jwt);

        // When
        Jwt result = jwtService.decode(testTokenValue);

        // Then
        assertThat(result).isEqualTo(jwt);
        verify(jwtDecoder).decode(testTokenValue);
    }

    @Test
    @DisplayName("Should throw BadRequestException when token is invalid")
    void decode_ShouldThrowBadRequestException_WhenTokenInvalid() {
        // Given
        when(jwtDecoder.decode(testTokenValue))
                .thenThrow(new RuntimeException("Invalid token"));

        // When & Then
        assertThatThrownBy(() -> jwtService.decode(testTokenValue))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid JWT token");

        verify(jwtDecoder).decode(testTokenValue);
    }

    @Test
    @DisplayName("Should validate refresh token successfully")
    void validateRefreshToken_ShouldPass_WhenTokenValid() {
        // Given
        when(jwt.getClaim("type")).thenReturn("refresh");

        // When & Then (no exception should be thrown)
        jwtService.validateRefreshToken(jwt);

        verify(jwt).getClaim("type");
    }

    @Test
    @DisplayName("Should throw BadRequestException when refresh token is null")
    void validateRefreshToken_ShouldThrowBadRequestException_WhenTokenNull() {
        // Given
        Jwt nullToken = null;

        // When & Then
        assertThatThrownBy(() -> jwtService.validateRefreshToken(nullToken))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    @Test
    @DisplayName("Should throw BadRequestException when token type is not refresh")
    void validateRefreshToken_ShouldThrowBadRequestException_WhenWrongType() {
        // Given
        when(jwt.getClaim("type")).thenReturn("access");

        // When & Then
        assertThatThrownBy(() -> jwtService.validateRefreshToken(jwt))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid refresh token");

        verify(jwt).getClaim("type");
    }
}
