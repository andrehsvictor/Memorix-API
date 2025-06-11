package andrehsvictor.memorix.common.jwt;

import java.time.Instant;

import org.springframework.security.core.userdetails.UserDetails;
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
    private final JwtLifetimeProperties jwtLifetimeProperties;

    public Jwt issueAccessToken(UserDetails userDetails) {
        String subject = userDetails.getUsername();
        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtLifetimeProperties.getAccessTokenLifetime());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim("type", "access")
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }

    public Jwt issueRefreshToken(UserDetails userDetails) {
        String subject = userDetails.getUsername();
        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtLifetimeProperties.getRefreshTokenLifetime());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim("type", "refresh")
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }
}
