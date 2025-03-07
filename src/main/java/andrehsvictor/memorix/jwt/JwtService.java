package andrehsvictor.memorix.jwt;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.exception.UnauthorizedException;
import andrehsvictor.memorix.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${jwt.audience:memorix}")
    private String audience = "memorix";

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.access-token.lifespan:15m}")
    private Duration accessTokenLifespan = Duration.ofMinutes(15);

    @Value("${jwt.refresh-token.lifespan:1d}")
    private Duration refreshTokenLifespan = Duration.ofDays(1);

    public Long getCurrentUserId() {
        JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(token.getToken().getSubject());
    }

    public Jwt decode(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid token");
        }
    }

    public Jwt issue(User user, JwtType type) {
        switch (type) {
            case ACCESS:
                return issueAccessToken(user);
            case REFRESH:
                return issueRefreshToken(user);
            default:
                throw new IllegalArgumentException("Invalid token type");
        }
    }

    public Jwt issue(Jwt refreshToken, JwtType type) {
        switch (type) {
            case ACCESS:
                return issueAccessToken(refreshToken);
            case REFRESH:
                return issueRefreshToken(refreshToken);
            default:
                throw new IllegalArgumentException("Invalid token type");
        }
    }

    private Jwt issueAccessToken(User user) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getDisplayName())
                .claim("preferred_username", user.getUsername())
                .claim("email_verified", user.isEmailVerified())
                .claim("picture", user.getPictureUrl())
                .claim("jti", UUID.randomUUID().toString())
                .claim("type", "access")
                .audience(List.of(audience))
                .issuer(issuer)
                .expiresAt(Instant.now().plus(accessTokenLifespan))
                .issuedAt(Instant.now())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }

    private Jwt issueRefreshToken(User user) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getId().toString())
                .claim("jti", UUID.randomUUID().toString())
                .claim("type", "refresh")
                .audience(List.of(audience))
                .issuer(issuer)
                .expiresAt(Instant.now().plus(refreshTokenLifespan))
                .issuedAt(Instant.now())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }

    private Jwt issueAccessToken(Jwt refreshToken) {
        Map<String, Object> claims = refreshToken.getClaims();
        if (!claims.get("type").equals("refresh")) {
            throw new UnauthorizedException("Token is not a refresh token");
        }

        JwtClaimsSet newClaims = JwtClaimsSet.builder()
                .subject(claims.get("sub").toString())
                .claim("jti", UUID.randomUUID().toString())
                .claim("type", "access")
                .audience(List.of(audience))
                .issuer(issuer)
                .expiresAt(Instant.now().plus(accessTokenLifespan))
                .issuedAt(Instant.now())
                .claim("email", claims.get("email"))
                .claim("name", claims.get("name"))
                .claim("preferred_username", claims.get("preferred_username"))
                .claim("email_verified", claims.get("email_verified"))
                .claim("picture", claims.get("picture"))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(newClaims));
    }

    private Jwt issueRefreshToken(Jwt refreshToken) {
        Map<String, Object> claims = refreshToken.getClaims();
        if (!claims.get("type").equals("refresh")) {
            throw new UnauthorizedException("Token is not a refresh token");
        }

        JwtClaimsSet newClaims = JwtClaimsSet.builder()
                .subject(claims.get("sub").toString())
                .claim("jti", UUID.randomUUID().toString())
                .claim("type", "refresh")
                .audience(List.of(audience))
                .issuer(issuer)
                .expiresAt(Instant.now().plus(refreshTokenLifespan))
                .issuedAt(Instant.now())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(newClaims));
    }
}
