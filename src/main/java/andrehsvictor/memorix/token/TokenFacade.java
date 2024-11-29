package andrehsvictor.memorix.token;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import andrehsvictor.memorix.authentication.AuthenticationService;
import andrehsvictor.memorix.token.accesstoken.AccessToken;
import andrehsvictor.memorix.token.accesstoken.AccessTokenService;
import andrehsvictor.memorix.token.dto.GetTokenDto;
import andrehsvictor.memorix.token.dto.PostTokenDto;
import andrehsvictor.memorix.token.refreshtoken.RefreshToken;
import andrehsvictor.memorix.token.refreshtoken.RefreshTokenService;
import andrehsvictor.memorix.token.revokedtoken.TokenRevocationService;
import andrehsvictor.memorix.user.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenFacade {

    private final TokenRevocationService tokenRevocationService;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;
    private final AuthenticationService authenticationService;

    public GetTokenDto issue(PostTokenDto postTokenDto) {
        Authentication authentication = authenticationService.authenticate(postTokenDto.getUsername(),
                postTokenDto.getPassword());
        User user = (User) authentication.getPrincipal();
        AccessToken accessToken = accessTokenService.issue(user.getId().toString());
        RefreshToken refreshToken = refreshTokenService.issue(user.getId());
        Long expiresIn = accessToken.getTtl();
        Long refreshExpiresIn = refreshToken.getTtl();
        return GetTokenDto.builder()
                .accessToken(accessToken.getTokenValue())
                .expiresIn(expiresIn)
                .refreshToken(refreshToken.getToken())
                .refreshTokenExpiresIn(refreshExpiresIn)
                .build();
    }
}
