package andrehsvictor.memorix.token;

import java.time.Duration;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.authentication.AuthenticationService;
import andrehsvictor.memorix.exception.UnauthorizedException;
import andrehsvictor.memorix.security.UserDetailsImpl;
import andrehsvictor.memorix.token.dto.GetTokenDto;
import andrehsvictor.memorix.token.dto.PostTokenDto;
import andrehsvictor.memorix.token.dto.TokenDto;
import andrehsvictor.memorix.token.jwt.JwtService;
import andrehsvictor.memorix.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final TokenRenewalService tokenRenewalService;
    private final TokenBlacklistService tokenBlacklistService;

    public GetTokenDto get(PostTokenDto postTokenDto) {
        String username = postTokenDto.getUsername();
        String password = postTokenDto.getPassword();
        UserDetailsImpl userDetails = (UserDetailsImpl) authenticationService.authenticate(username, password)
                .getPrincipal();
        User user = userDetails.getUser();
        String subject = user.getId().toString();
        return getTokenDto(subject);
    }

    public void revoke(TokenDto tokenDto) {
        Jwt jwt = jwtService.decode(tokenDto.getToken());
        tokenBlacklistService.save(jwt.getId(), getRemainingDuration(jwt));
    }

    public GetTokenDto refresh(TokenDto tokenDto) {
        Jwt jwt = jwtService.decode(tokenDto.getToken());
        boolean isRevoked = tokenBlacklistService.exists(jwt.getId());
        boolean isValid = tokenRenewalService.exists(jwt.getId());
        if (isRevoked || !isValid) {
            throw new UnauthorizedException("Refresh token is invalid or revoked.");
        }
        tokenRenewalService.delete(jwt.getId());
        return getTokenDto(jwt.getSubject());
    }

    private GetTokenDto getTokenDto(String subject) {
        Jwt accessToken = jwtService.issueAccessToken(subject);
        Jwt refreshToken = jwtService.issueRefreshToken(subject);
        tokenRenewalService.save(refreshToken.getId(), getDuration(refreshToken));
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
