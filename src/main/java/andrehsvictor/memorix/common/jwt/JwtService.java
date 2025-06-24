package andrehsvictor.memorix.common.jwt;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.common.exception.BadRequestException;
import andrehsvictor.memorix.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtLifetimeProperties jwtLifetimeProperties;

    public UUID getCurrentUserUuid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            return UUID.fromString(jwtAuthenticationToken.getToken().getSubject());
        }
        return null;
    }

    public Jwt issueAccessToken(String subject) {
        return createToken(subject, "access", jwtLifetimeProperties.getAccessTokenLifetime());
    }

    public Jwt issueRefreshToken(String subject) {
        return createToken(subject, "refresh", jwtLifetimeProperties.getRefreshTokenLifetime());
    }

    public Jwt decode(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            throw new UnauthorizedException();
        }
    }

    public void validateRefreshToken(Jwt token) {
        if (token == null || !"refresh".equals(token.getClaim("type"))) {
            throw new BadRequestException("Invalid refresh token");
        }
    }

    private Jwt createToken(String subject, String type, Duration lifetime) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(now)
                .expiresAt(now.plus(lifetime))
                .id(UUID.randomUUID().toString())
                .claim("type", type)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }
}