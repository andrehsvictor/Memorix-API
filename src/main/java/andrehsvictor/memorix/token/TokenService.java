package andrehsvictor.memorix.token;

import java.time.Duration;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.authentication.AuthenticationService;
import andrehsvictor.memorix.exception.UnauthorizedException;
import andrehsvictor.memorix.security.UserDetailsImpl;
import andrehsvictor.memorix.token.dto.GetTokenDto;
import andrehsvictor.memorix.token.dto.PostTokenDto;
import andrehsvictor.memorix.token.dto.JwtTokenDto;
import andrehsvictor.memorix.token.jwt.JwtService;
import andrehsvictor.memorix.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    public GetTokenDto get(PostTokenDto postTokenDto) {
        String email = postTokenDto.getEmail();
        String password = postTokenDto.getPassword();
        UserDetailsImpl userDetails = (UserDetailsImpl) authenticationService.authenticate(email, password)
                .getPrincipal();
        User user = userDetails.getUser();
        String subject = user.getId().toString();
        return getTokenDto(subject);
    }

    public void revoke(JwtTokenDto jwtTokenDto) {
        Jwt jwt = jwtService.decode(jwtTokenDto.getToken());
        tokenBlacklistService.revoke(jwt.getId(), getRemainingDuration(jwt));
    }

    public GetTokenDto refresh(JwtTokenDto jwtTokenDto) {
        Jwt jwt = jwtService.decode(jwtTokenDto.getToken());
        boolean isRevoked = tokenBlacklistService.isRevoked(jwt.getId());
        boolean isValid = refreshTokenService.exists(jwt.getId());
        if (isRevoked || !isValid) {
            throw new UnauthorizedException("Refresh token is invalid or revoked.");
        }
        refreshTokenService.delete(jwt.getId());
        return getTokenDto(jwt.getSubject());
    }

    private GetTokenDto getTokenDto(String subject) {
        Jwt accessToken = jwtService.issueAccessToken(subject);
        Jwt refreshToken = jwtService.issueRefreshToken(subject);
        refreshTokenService.save(refreshToken.getId(), getDuration(refreshToken));
        return GetTokenDto.builder()
                .accessToken(accessToken.getTokenValue())
                .refreshToken(refreshToken.getTokenValue())
                .expiresIn(getDuration(accessToken).getSeconds())
                .build();
    }

    private Duration getDuration(Jwt jwt) {
        return Duration.ofSeconds(jwt.getExpiresAt().getEpochSecond() - jwt.getIssuedAt().getEpochSecond());
    }

    private Duration getRemainingDuration(Jwt jwt) {
        return Duration.ofSeconds(jwt.getExpiresAt().getEpochSecond() - System.currentTimeMillis() / 1000);
    }
}
