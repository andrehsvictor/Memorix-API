package andrehsvictor.memorix.common.revokedtoken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
@DisplayName("RevokedTokenService Tests")
class RevokedTokenServiceTest {

    @Mock
    private RedisTemplate<String, Integer> redisTemplate;

    @Mock
    private ValueOperations<String, Integer> valueOperations;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private RevokedTokenService revokedTokenService;

    private String testTokenId;
    private String expectedRedisKey;
    private Instant now;
    private Instant expiration;

    @BeforeEach
    void setUp() {
        testTokenId = "test-token-id";
        expectedRedisKey = "revoked_token:" + testTokenId;
        now = Instant.now();
        expiration = now.plusSeconds(3600);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(jwt.getId()).thenReturn(testTokenId);
        when(jwt.getExpiresAt()).thenReturn(expiration);
    }

    @Test
    @DisplayName("Should revoke token successfully")
    void revoke_ShouldStoreTokenInRedis_WhenTokenValid() {
        // When
        revokedTokenService.revoke(jwt);

        // Then
        verify(jwt).getId();
        verify(jwt).getExpiresAt();
        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(eq(expectedRedisKey), eq(0), any(Duration.class));
    }

    @Test
    @DisplayName("Should return true when token is revoked")
    void isRevoked_ShouldReturnTrue_WhenTokenExistsInRedis() {
        // Given
        when(redisTemplate.hasKey(expectedRedisKey)).thenReturn(true);

        // When
        boolean result = revokedTokenService.isRevoked(jwt);

        // Then
        assertThat(result).isTrue();
        verify(jwt).getId();
        verify(redisTemplate).hasKey(expectedRedisKey);
    }

    @Test
    @DisplayName("Should return false when token is not revoked")
    void isRevoked_ShouldReturnFalse_WhenTokenNotInRedis() {
        // Given
        when(redisTemplate.hasKey(expectedRedisKey)).thenReturn(false);

        // When
        boolean result = revokedTokenService.isRevoked(jwt);

        // Then
        assertThat(result).isFalse();
        verify(jwt).getId();
        verify(redisTemplate).hasKey(expectedRedisKey);
    }

    @Test
    @DisplayName("Should handle null response from Redis hasKey")
    void isRevoked_ShouldReturnFalse_WhenRedisReturnsNull() {
        // Given
        when(redisTemplate.hasKey(expectedRedisKey)).thenReturn(null);

        // When
        boolean result = revokedTokenService.isRevoked(jwt);

        // Then
        assertThat(result).isFalse();
        verify(jwt).getId();
        verify(redisTemplate).hasKey(expectedRedisKey);
    }
}
