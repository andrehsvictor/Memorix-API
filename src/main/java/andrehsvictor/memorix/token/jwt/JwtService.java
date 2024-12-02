package andrehsvictor.memorix.token.jwt;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class JwtService {

    @NotEmpty(message = "The access token issuer must be provided.")
    @Value("${memorix.security.jwt.issuer:localhost}")
    private String issuer = "localhost";

    @NotEmpty(message = "The access token audience must be provided.")
    @Value("${memorix.security.jwt.audience:memorix}")
    private String audience = "memorix";

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public Jwt issue(String subject, JwtType type, Duration expiresIn) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(subject)
                .issuer(issuer)
                .id(UUID.randomUUID().toString())
                .claim("aud", audience)
                .claim("type", type.getType())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(expiresIn))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }

    public Jwt decode(String token) {
        return jwtDecoder.decode(token);
    }

    public Long getRemainingLifespan(String token, TimeUnit timeUnit) {
        Jwt jwt = jwtDecoder.decode(token);
        Instant expiresAt = jwt.getExpiresAt();
        Instant now = Instant.now();
        return timeUnit.convert(Duration.between(now, expiresAt).toMillis(), TimeUnit.MILLISECONDS);
    }

    public String getJti(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getId();
    }

    public String getSubject(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

}
