package andrehsvictor.memorix.token;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenRenewalService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "refresh_token:";

    public void save(String jti, Duration lifespan) {
        redisTemplate.opsForValue().set(PREFIX + jti, jti, lifespan);
    }

    public String get(String jti) {
        return redisTemplate.opsForValue().get(PREFIX + jti);
    }

    public boolean exists(String jti) {
        return redisTemplate.hasKey(PREFIX + jti);
    }

    public void delete(String jti) {
        redisTemplate.delete(PREFIX + jti);
    }
}
