package andrehsvictor.memorix.common.jwt;

import java.time.Duration;
import java.time.Instant;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtLifetimeProperties jwtLifetimeProperties;

    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    public Jwt issueAccessToken(UserDetails userDetails) {
        return createToken(userDetails.getUsername(), ACCESS_TOKEN_TYPE,
                jwtLifetimeProperties.getAccessTokenLifetime());
    }

    public Jwt issueRefreshToken(UserDetails userDetails) {
        return createToken(userDetails.getUsername(), REFRESH_TOKEN_TYPE,
                jwtLifetimeProperties.getRefreshTokenLifetime());
    }

    public Jwt issueAccessToken(Jwt refreshToken) {
        validateRefreshToken(refreshToken);
        return createToken(refreshToken.getSubject(), ACCESS_TOKEN_TYPE,
                jwtLifetimeProperties.getAccessTokenLifetime());
    }

    public Jwt issueRefreshToken(Jwt refreshToken) {
        validateRefreshToken(refreshToken);
        return createToken(refreshToken.getSubject(), REFRESH_TOKEN_TYPE,
                jwtLifetimeProperties.getRefreshTokenLifetime());
    }

    public Jwt decode(String token) {
        try {
            return jwtDecoder.decode(token);
        } catch (Exception e) {
            throw new BadRequestException("Invalid JWT token: " + e.getMessage());
        }
    }

    private Jwt createToken(String subject, String tokenType, Duration lifetime) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(lifetime);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim("type", tokenType)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }

    private void validateRefreshToken(Jwt refreshToken) {
        if (refreshToken == null ||
                !REFRESH_TOKEN_TYPE.equals(refreshToken.getClaim("type"))) {
            throw new BadRequestException(
                    "Invalid refresh token. The token must be a valid refresh token.");
        }
    }
}