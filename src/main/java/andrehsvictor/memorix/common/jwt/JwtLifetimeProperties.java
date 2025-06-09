package andrehsvictor.memorix.common.jwt;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
public class JwtLifetimeProperties {

    @Value("${memorix.jwt.access-token.lifetime:15m}")
    private Duration accessTokenLifetime = Duration.ofMinutes(15);

    @Value("${memorix.jwt.refresh-token.lifetime:1h}")
    private Duration refreshTokenLifetime = Duration.ofHours(1);

}
