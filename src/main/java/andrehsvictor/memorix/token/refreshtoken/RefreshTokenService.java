package andrehsvictor.memorix.token.refreshtoken;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.exception.UnauthorizedException;
import andrehsvictor.memorix.token.jwt.JwtService;
import andrehsvictor.memorix.token.jwt.JwtType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${memorix.jwt.refresh-token.lifespan:30d}")
    private Duration lifespan = Duration.ofDays(30);

    public Jwt issue(UUID userId) {
        Jwt jwt = jwtService.issue(userId.toString(), JwtType.REFRESH, lifespan);
        RefreshToken refreshToken = RefreshToken.of(UUID.fromString(jwt.getId()));
        refreshToken.setTtl(lifespan.toSeconds());
        refreshTokenRepository.save(refreshToken);
        return jwt;
    }

    public boolean existsById(UUID id) {
        return refreshTokenRepository.existsById(id);
    }

    public void assertExistsById(UUID id) {
        if (!existsById(id)) {
            throw new UnauthorizedException("Invalid or expired refresh token.");
        }
    }

    public void deleteById(UUID id) {
        refreshTokenRepository.deleteById(id);
    }
}
