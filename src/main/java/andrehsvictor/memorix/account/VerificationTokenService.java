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
public class VerificationTokenService {

    private final RedisTemplate<String, Long> redisTemplate;

    private final static String PREFIX = "verification_token:";

    @Value("${token.verification.lifespan:6h}")
    private Duration ttl = Duration.ofHours(6);

    public Long get(String token) {
        if (!redisTemplate.hasKey(PREFIX + token)) {
            throw new UnauthorizedException("Invalid token");
        }
        return redisTemplate.opsForValue().get(PREFIX + token);
    }

    public String generate(Long userId) {
        String token = Base64.getUrlEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
        redisTemplate.opsForValue().set(PREFIX + token, userId, ttl);
        return token;
    }

    public void delete(String token) {
        redisTemplate.delete(PREFIX + token);
    }

}
