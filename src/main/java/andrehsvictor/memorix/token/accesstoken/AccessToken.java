package andrehsvictor.memorix.token.accesstoken;

import java.util.concurrent.TimeUnit;

import org.springframework.security.oauth2.jwt.Jwt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessToken {
    private Jwt jwt;

    public AccessToken(Jwt jwt) {
        this.jwt = jwt;
    }

    public Long getTtl(TimeUnit timeUnit) {
        return timeUnit.convert(jwt.getExpiresAt().toEpochMilli() - System.currentTimeMillis(), timeUnit);
    }

    public static AccessToken of(Jwt jwt) {
        return new AccessToken(jwt);
    }
}
