package andrehsvictor.memorix.token.jwt;

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

    public Jwt issue(String subject, String type, Duration expiry) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(subject)
                .issuer(issuer)
                .id(UUID.randomUUID().toString())
                .claim("aud", audience)
                .claim("type", type)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(expiry))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }

}
