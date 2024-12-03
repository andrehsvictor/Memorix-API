package andrehsvictor.memorix.token.jwt;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${memorix.security.jwt.access-token.lifespan:15m}")
    private Duration accessTokenLifespan = Duration.ofMinutes(15);

    @Value("${memorix.security.jwt.refresh-token.lifespan:7d}")
    private Duration refreshTokenLifespan = Duration.ofDays(7);

    @Value("${memorix.security.jwt.issuer:localhost}")
    private String issuer = "localhost";

    @Value("${memorix.security.jwt.audience:memorix}")
    private List<String> audience = List.of("memorix");

    public Jwt issueAccessToken(String subject) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(subject)
                .audience(audience)
                .issuer(issuer)
                .id(UUID.randomUUID().toString())
                .claim("type", JwtType.ACCESS)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(accessTokenLifespan))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }

    public Jwt issueRefreshToken(String subject) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(subject)
                .audience(audience)
                .issuer(issuer)
                .id(UUID.randomUUID().toString())
                .claim("type", JwtType.REFRESH)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(refreshTokenLifespan))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }

    public Jwt decode(String token) {
        return jwtDecoder.decode(token);
    }
}
