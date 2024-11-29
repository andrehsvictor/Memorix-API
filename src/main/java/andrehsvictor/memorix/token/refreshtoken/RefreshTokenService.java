package andrehsvictor.memorix.token.refreshtoken;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.token.jwt.JwtService;
import andrehsvictor.memorix.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${memorix.security.jwt.refresh-token.expiry:PT1D}")
    private Duration expiry = Duration.ofDays(1);

    public RefreshToken issue(User user) {
        String token = jwtService.issue(user).getTokenValue();
        LocalDateTime expiresAt = LocalDateTime.now().plus(expiry);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiresAt(expiresAt)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }
}
