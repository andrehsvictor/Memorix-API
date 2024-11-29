package andrehsvictor.memorix.token.refreshtoken;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.token.jwt.JwtService;
import andrehsvictor.memorix.token.jwt.JwtType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${memorix.security.jwt.refresh-token.expiry:PT24H}")
    private Duration expiry = Duration.ofHours(24);

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    public boolean existsByToken(String token) {
        return refreshTokenRepository.existsByToken(token);
    }

    public RefreshToken issue(UUID userId) {
        Long ttl = expiry.toSeconds();
        String token = jwtService.issue(userId.toString(), JwtType.REFRESH, expiry).getTokenValue();
        RefreshToken refreshToken = new RefreshToken(token, ttl, userId);
        return refreshTokenRepository.save(refreshToken);
    }
}
