package andrehsvictor.memorix.account;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResetPasswordTokenService {

    private final StringRedisTemplate redisTemplate;

}
