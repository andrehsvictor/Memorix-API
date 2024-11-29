package andrehsvictor.memorix.token.refreshtoken;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import andrehsvictor.memorix.exception.UnauthorizedException;
import andrehsvictor.memorix.token.jwt.JwtService;
import andrehsvictor.memorix.token.jwt.JwtType;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @NotNull(message = "The refresh token expires in must be set.")
    @Value("${memorix.security.jwt.refresh-token.expires-in:1d}")
    private Duration expiresIn = Duration.ofDays(1);

    public RefreshToken issue(UUID userId) {
        String token = jwtService.issue(userId.toString(), JwtType.REFRESH, expiresIn).getTokenValue();
        RefreshToken refreshToken = buildRefreshToken(userId, token);
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token."));
    }

    private RefreshToken buildRefreshToken(UUID userId, String token) {
        return RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiresIn(expiresIn.getSeconds())
                .build();
    }
}
