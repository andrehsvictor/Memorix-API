package andrehsvictor.memorix.token;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import andrehsvictor.memorix.token.dto.GetTokenDto;
import andrehsvictor.memorix.token.jwt.JwtService;
import andrehsvictor.memorix.token.refreshtoken.RefreshToken;
import andrehsvictor.memorix.token.refreshtoken.RefreshTokenService;
import andrehsvictor.memorix.user.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenFacade {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public GetTokenDto issue(User user) {
        Jwt jwt = jwtService.issue(user);
        RefreshToken refreshToken = refreshTokenService.issue(user);
        Long expiresIn = jwt.getExpiresAt().toEpochMilli() - Instant.now().toEpochMilli();
        Long refreshTokenExpiresIn = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                - refreshToken.getExpiresAt().toEpochSecond(ZoneOffset.UTC);
        return GetTokenDto.builder()
                .accessToken(jwt.getTokenValue())
                .refreshToken(refreshToken.getToken())
                .expiresIn(expiresIn)
                .refreshTokenExpiresIn(refreshTokenExpiresIn)
                .build();
    }
}
