package andrehsvictor.memorix.account;

import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationTokenRepository {

    private final StringRedisTemplate redisTemplate;

    private final static String PREFIX = "verification_token:";

    @Value("${token.activation.ttl:6h}")
    private Duration ttl = Duration.ofHours(6);

    public String get(String token) {
        return redisTemplate.opsForValue().get(PREFIX + token);
    }

    public String generate(String email) {
        String token = Base64.getUrlEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
        redisTemplate.opsForValue().set(PREFIX + token, email, ttl);
        return token;
    }

    public boolean exists(String token) {
        return redisTemplate.hasKey(PREFIX + token);
    }

    public void delete(String token) {
        redisTemplate.delete(PREFIX + token);
    }

}
