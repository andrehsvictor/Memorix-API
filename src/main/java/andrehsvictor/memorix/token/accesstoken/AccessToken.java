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

    public Long getExpiresIn(TimeUnit timeUnit) {
        Long expiration = jwt.getExpiresAt().toEpochMilli();
        Long now = System.currentTimeMillis();
        Long timeToLive = expiration - now;
        return timeUnit.convert(timeToLive, TimeUnit.MILLISECONDS);
    }

    public String getToken() {
        return jwt.getTokenValue();
    }

    public static AccessToken of(Jwt jwt) {
        return new AccessToken(jwt);
    }
}
