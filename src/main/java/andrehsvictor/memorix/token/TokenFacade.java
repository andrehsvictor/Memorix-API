package andrehsvictor.memorix.token;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.authentication.AuthenticationService;
import andrehsvictor.memorix.exception.UnauthorizedException;
import andrehsvictor.memorix.security.UserDetailsImpl;
import andrehsvictor.memorix.token.accesstoken.AccessToken;
import andrehsvictor.memorix.token.accesstoken.AccessTokenService;
import andrehsvictor.memorix.token.dto.GetTokenDto;
import andrehsvictor.memorix.token.dto.PostTokenDto;
import andrehsvictor.memorix.token.dto.RefreshTokenDto;
import andrehsvictor.memorix.token.refreshtoken.RefreshToken;
import andrehsvictor.memorix.token.refreshtoken.RefreshTokenService;
import andrehsvictor.memorix.token.revokedtoken.RevokedTokenService;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenFacade {

    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationService authenticationService;
    private final RevokedTokenService revokedTokenService;
    private final UserService userService;

    public GetTokenDto getToken(PostTokenDto postTokenDto) {
        String username = postTokenDto.getUsername();
        String password = postTokenDto.getPassword();
        UserDetailsImpl userDetails = (UserDetailsImpl) authenticationService.authenticate(username, password).getPrincipal();
        return buildGetTokenDto(userDetails.getUser());
    }

    public GetTokenDto refreshToken(RefreshTokenDto refreshTokenDto) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenDto.getRefreshToken());
        if (revokedTokenService.isRevoked(refreshTokenDto.getRefreshToken())) {
            throw new UnauthorizedException("This token has been revoked.");
        }
        revokedTokenService.revoke(refreshTokenDto.getRefreshToken());
        User user = userService.findById(refreshToken.getUserId());
        return buildGetTokenDto(user);
    }

    private GetTokenDto buildGetTokenDto(User user) {
        AccessToken accessToken = accessTokenService.issue(user.getId().toString());
        RefreshToken refreshToken = refreshTokenService.issue(user.getId());
        Long expiresIn = accessToken.getExpiresIn(TimeUnit.SECONDS);
        Long refreshTokenExpiresIn = refreshToken.getExpiresIn(TimeUnit.SECONDS);
        return GetTokenDto.builder()
                .accessToken(accessToken.getToken())
                .refreshToken(refreshToken.getToken())
                .expiresIn(expiresIn)
                .refreshExpiresIn(refreshTokenExpiresIn)
                .build();
    }

}
