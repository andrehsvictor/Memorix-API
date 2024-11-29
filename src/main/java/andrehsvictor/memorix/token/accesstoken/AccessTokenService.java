package andrehsvictor.memorix.token.accesstoken;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.token.jwt.JwtService;
import andrehsvictor.memorix.token.jwt.JwtType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessTokenService {

    private final JwtService jwtService;

    @Value("${memorix.security.jwt.access-token.expiry:PT15M}")
    private Duration expiry = Duration.ofMinutes(15);

    public AccessToken issue(String subject) {
        Jwt jwt = jwtService.issue(subject, JwtType.ACCESS, expiry);
        return new AccessToken(jwt);
    }
}
