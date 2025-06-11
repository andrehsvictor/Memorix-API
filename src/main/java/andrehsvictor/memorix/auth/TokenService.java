package andrehsvictor.memorix.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.auth.dto.CredentialsDto;
import andrehsvictor.memorix.auth.dto.IdTokenDto;
import andrehsvictor.memorix.auth.dto.RefreshTokenDto;
import andrehsvictor.memorix.auth.dto.RevokeTokenDto;
import andrehsvictor.memorix.auth.dto.TokenDto;
import andrehsvictor.memorix.common.google.GoogleAuthenticationService;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.common.revokedtoken.RevokedTokenService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final RevokedTokenService revokedTokenService;
    private final GoogleAuthenticationService googleAuthenticationService;

    public TokenDto request(CredentialsDto credentialsDto) {
        Authentication authentication = authenticationService.authenticate(
                credentialsDto.getUsername(),
                credentialsDto.getPassword());
        return createTokenPair(authentication.getName());
    }

    public TokenDto google(IdTokenDto idTokenDto) {
        Authentication authentication = googleAuthenticationService.authenticate(idTokenDto.getIdToken());
        return createTokenPair(authentication.getName());
    }

    public TokenDto refresh(RefreshTokenDto refreshTokenDto) {
        Jwt refreshToken = jwtService.decode(refreshTokenDto.getRefreshToken());
        revokedTokenService.revoke(refreshToken);
        return createTokenPair(refreshToken.getSubject());
    }

    public void revoke(RevokeTokenDto revokeTokenDto) {
        Jwt jwt = jwtService.decode(revokeTokenDto.getToken());
        revokedTokenService.revoke(jwt);
    }

    private TokenDto createTokenPair(String subject) {
        Jwt accessToken = jwtService.issueAccessToken(subject);
        Jwt refreshToken = jwtService.issueRefreshToken(subject);
        Long expiresIn = accessToken.getExpiresAt().getEpochSecond() - accessToken.getIssuedAt().getEpochSecond();

        return TokenDto.builder()
                .accessToken(accessToken.getTokenValue())
                .refreshToken(refreshToken.getTokenValue())
                .expiresIn(expiresIn)
                .build();
    }
}