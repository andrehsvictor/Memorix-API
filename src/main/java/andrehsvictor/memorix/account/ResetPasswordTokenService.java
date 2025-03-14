package andrehsvictor.memorix.account;

import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResetPasswordTokenService {

    private final RedisTemplate<String, Long> redisTemplate;

    @Value("${token.reset-password.lifespan:1h}")
    private Duration ttl = Duration.ofHours(1);

    private static final String PREFIX = "reset_password_token:";

    public String generate(Long userId) {
        String token = Base64.getUrlEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
        redisTemplate.opsForValue().set(PREFIX + token, userId, ttl);
        return token;
    }

    public Long get(String token) {
        if (!redisTemplate.hasKey(PREFIX + token)) {
            throw new UnauthorizedException("Invalid token: " + token);
        }
        return redisTemplate.opsForValue().get(PREFIX + token);
    }

    public void delete(String token) {
        redisTemplate.delete(PREFIX + token);
    }

}
