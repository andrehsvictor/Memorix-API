package andrehsvictor.memorix.token;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.authentication.AuthenticationService;
import andrehsvictor.memorix.security.UserDetailsImpl;
import andrehsvictor.memorix.token.accesstoken.AccessTokenService;
import andrehsvictor.memorix.token.dto.GetTokenDto;
import andrehsvictor.memorix.token.dto.PostTokenDto;
import andrehsvictor.memorix.token.dto.TokenDto;
import andrehsvictor.memorix.token.jwt.JwtService;
import andrehsvictor.memorix.token.refreshtoken.RefreshTokenService;
import andrehsvictor.memorix.token.revokedtoken.RevokedTokenService;
import andrehsvictor.memorix.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenFacade {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RevokedTokenService revokedTokenService;
    private final AccessTokenService accessTokenService;
    private final AuthenticationService authenticationService;

    public GetTokenDto request(PostTokenDto postTokenDto) {
        String username = postTokenDto.getUsername();
        String password = postTokenDto.getPassword();
        UserDetailsImpl userDetails = (UserDetailsImpl) authenticationService.authenticate(username, password)
                .getPrincipal();
        User user = userDetails.getUser();
        return getTokenDto(user.getId());
    }

    public GetTokenDto refresh(TokenDto tokenDto) {
        Jwt refreshToken = jwtService.decode(tokenDto.getToken());
        refreshTokenService.assertExistsById(UUID.fromString(refreshToken.getId()));
        revokedTokenService.revoke(tokenDto.getToken());
        UUID userId = UUID.fromString(refreshToken.getSubject());
        return getTokenDto(userId);
    }

    public void revoke(TokenDto tokenDto) {
        revokedTokenService.revoke(tokenDto.getToken());
    }

    private GetTokenDto getTokenDto(UUID userId) {
        Jwt accessToken = accessTokenService.issue(userId);
        Jwt refreshToken = refreshTokenService.issue(userId);
        Long expiresIn = jwtService.getRemainingLifespan(accessToken.getTokenValue(), TimeUnit.SECONDS);
        return GetTokenDto.builder()
                .accessToken(accessToken.getTokenValue())
                .refreshToken(refreshToken.getTokenValue())
                .expiresIn(expiresIn)
                .build();
    }

}
