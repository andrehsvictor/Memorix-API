package andrehsvictor.memorix.token;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.authentication.AuthenticationService;
import andrehsvictor.memorix.jwt.JwtService;
import andrehsvictor.memorix.jwt.JwtType;
import andrehsvictor.memorix.revokedtoken.RevokedTokenService;
import andrehsvictor.memorix.security.impl.UserDetailsImpl;
import andrehsvictor.memorix.token.dto.AccessTokenDto;
import andrehsvictor.memorix.token.dto.CredentialsDto;
import andrehsvictor.memorix.token.dto.TokenDto;
import andrehsvictor.memorix.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final RevokedTokenService revokedTokenService;

    public AccessTokenDto generate(CredentialsDto credentials) {
        Authentication authentication = authenticationService.authenticate(
                credentials.getUsername(),
                credentials.getPassword());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        Jwt accessToken = jwtService.issue(user, JwtType.ACCESS);
        Jwt refreshToken = jwtService.issue(user, JwtType.REFRESH);

        return AccessTokenDto.builder()
                .accessToken(accessToken.getTokenValue())
                .refreshToken(refreshToken.getTokenValue())
                .build();
    }

    public AccessTokenDto refresh(TokenDto tokenDto) {
        Jwt refreshToken = jwtService.decode(tokenDto.getToken());
        Jwt accessToken = jwtService.issue(refreshToken, JwtType.ACCESS);
        Jwt newRefreshToken = jwtService.issue(refreshToken, JwtType.REFRESH);
        revokedTokenService.revoke(refreshToken);
        return AccessTokenDto.builder()
                .accessToken(accessToken.getTokenValue())
                .refreshToken(newRefreshToken.getTokenValue())
                .build();
    }

    public void revoke(TokenDto tokenDto) {
        Jwt token = jwtService.decode(tokenDto.getToken());
        revokedTokenService.revoke(token);
    }

}