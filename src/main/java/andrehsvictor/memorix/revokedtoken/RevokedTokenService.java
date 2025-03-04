package andrehsvictor.memorix.revokedtoken;

import java.time.Duration;
import java.time.Instant;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RevokedTokenService {

    private final StringRedisTemplate redisTemplate;

    public void revoke(Jwt jwt) {
        Duration duration = Duration.between(Instant.now(), jwt.getExpiresAt());
        redisTemplate.opsForValue().set(jwt.getId(), "", duration);
    }

    public boolean isRevoked(Jwt jwt) {
        return redisTemplate.hasKey(jwt.getId());
    }
}
