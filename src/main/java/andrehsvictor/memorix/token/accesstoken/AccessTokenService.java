package andrehsvictor.memorix.token.accesstoken;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import andrehsvictor.memorix.token.jwt.JwtService;
import andrehsvictor.memorix.token.jwt.JwtType;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class AccessTokenService {

    private final JwtService jwtService;

    @NotNull(message = "The access token expires in must be set.")
    @Value("${memorix.security.jwt.access-token.expires-in:15m}")
    private Duration expiresIn = Duration.ofMinutes(15);

    public AccessToken issue(UUID userId) {
        Jwt jwt = jwtService.issue(userId.toString(), JwtType.ACCESS, expiresIn);
        return AccessToken.of(jwt, expiresIn.getSeconds());
    }
}
