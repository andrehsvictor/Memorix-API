package andrehsvictor.memorix.token.accesstoken;

import java.util.concurrent.TimeUnit;

import org.springframework.security.oauth2.jwt.Jwt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessToken {
    private Jwt jwt;
    private Long expiresIn;

    public AccessToken(Jwt jwt, Long expiresIn) {
        this.jwt = jwt;
        this.expiresIn = expiresIn;
    }

    public Long getExpiresIn(TimeUnit timeUnit) {
        return timeUnit.convert(expiresIn, TimeUnit.SECONDS);
    }

    public String getToken() {
        return jwt.getTokenValue();
    }

    public static AccessToken of(Jwt jwt, Long expiresIn) {
        return new AccessToken(jwt, expiresIn);
    }
}
