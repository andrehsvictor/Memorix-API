package andrehsvictor.memorix.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.auth.dto.CredentialsDto;
import andrehsvictor.memorix.auth.dto.RefreshTokenDto;
import andrehsvictor.memorix.auth.dto.RevokeTokenDto;
import andrehsvictor.memorix.auth.dto.TokenDto;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.common.revokedtoken.RevokedTokenService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final RevokedTokenService revokedTokenService;

    public TokenDto request(CredentialsDto credentialsDto) {
        Authentication authentication = authenticationService.authenticate(
                credentialsDto.getUsername(),
                credentialsDto.getPassword());
        Jwt accessToken = jwtService.issueAccessToken(authentication.getName());
        Jwt refreshToken = jwtService.issueRefreshToken(authentication.getName());
        Long expiresIn = accessToken.getExpiresAt().getEpochSecond() - accessToken.getIssuedAt().getEpochSecond();
        return TokenDto.builder()
                .accessToken(accessToken.getTokenValue())
                .refreshToken(refreshToken.getTokenValue())
                .expiresIn(expiresIn)
                .build();
    }

    public TokenDto refresh(RefreshTokenDto refreshTokenDto) {
        Jwt refreshToken = jwtService.decode(refreshTokenDto.getRefreshToken());
        Jwt accessToken = jwtService.issueAccessToken(refreshToken.getSubject());
        Jwt newRefreshToken = jwtService.issueRefreshToken(refreshToken.getSubject());
        Long expiresIn = accessToken.getExpiresAt().getEpochSecond() - accessToken.getIssuedAt().getEpochSecond();
        revokedTokenService.revoke(refreshToken);
        return TokenDto.builder()
                .accessToken(accessToken.getTokenValue())
                .refreshToken(newRefreshToken.getTokenValue())
                .expiresIn(expiresIn)
                .build();
    }

    public void revoke(RevokeTokenDto revokeTokenDto) {
        Jwt jwt = jwtService.decode(revokeTokenDto.getToken());
        revokedTokenService.revoke(jwt);
    }

}
