package andrehsvictor.memorix.token.accesstoken;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import andrehsvictor.memorix.user.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class AccessTokenService {

    @NotNull(message = "The access token expiry must be provided.")
    @Value("${memorix.security.jwt.access-token.expiry:PT15M}")
    private Duration expiry = Duration.ofMinutes(15);

    @NotEmpty(message = "The access token issuer must be provided.")
    @Value("${memorix.security.jwt.access-token.issuer}")
    private String issuer = "localhost";

    @NotEmpty(message = "The access token audience must be provided.")
    @Value("${memorix.security.jwt.access-token.audience}")
    private String audience = "memorix";

    private final JwtEncoder jwtEncoder;

    public Jwt issue(User user) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getId().toString())
                .issuer(issuer)
                .id(UUID.randomUUID().toString())
                .claim("aud", audience)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(expiry))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }

}
