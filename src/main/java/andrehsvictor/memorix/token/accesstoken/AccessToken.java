package andrehsvictor.memorix.token.accesstoken;

import java.time.Instant;

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

    public String getSubject() {
        return jwt.getSubject();
    }

    public String getIssuer() {
        return jwt.getIssuer().toString();
    }

    public String getAudience() {
        return jwt.getAudience().toString();
    }

    public Instant getIssuedAt() {
        return jwt.getIssuedAt();
    }

    public Instant getExpiresAt() {
        return jwt.getExpiresAt();
    }

    public Long getTtl() {
        return jwt.getExpiresAt().getEpochSecond() - jwt.getIssuedAt().getEpochSecond();
    }

    public String getType() {
        return jwt.getClaimAsString("type");
    }

    public String getTokenValue() {
        return jwt.getTokenValue();
    }
}
