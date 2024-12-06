package andrehsvictor.memorix.token;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "revoked_token:";

    public void save(String jti, Duration lifespan) {
        redisTemplate.opsForValue().set(PREFIX + jti, jti, lifespan);
    }

    public String get(String jti) {
        return redisTemplate.opsForValue().get(PREFIX + jti);
    }

    public boolean exists(String jti) {
        return redisTemplate.hasKey(PREFIX + jti);
    }
}
