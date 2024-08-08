package andrehsvictor.memorix.controller;

import java.time.ZoneOffset;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.dto.ResponseBody;
import andrehsvictor.memorix.dto.request.SigninRequestDTO;
import andrehsvictor.memorix.dto.response.TokenResponseDTO;
import andrehsvictor.memorix.entity.RefreshToken;
import andrehsvictor.memorix.service.AccessTokenService;
import andrehsvictor.memorix.service.AuthService;
import andrehsvictor.memorix.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseBody<TokenResponseDTO> signin(@RequestBody SigninRequestDTO request) {
        Authentication authentication = authService.authenticate(request.getUsernameOrEmail(), request.getPassword());
        Jwt accessToken = accessTokenService.generate(authentication);
        RefreshToken refreshToken = refreshTokenService.generate(authentication);

        return ResponseBody.<TokenResponseDTO>builder()
                .data(TokenResponseDTO.builder()
                        .accessToken(accessToken.getTokenValue())
                        .refreshToken(refreshToken.getToken())
                        .accessTokenExpiry(accessToken.getExpiresAt().toEpochMilli())
                        .refreshTokenExpiry(refreshToken.getExpiresAt().toInstant(ZoneOffset.UTC).toEpochMilli())
                        .build())
                .build();
    }

    @PostMapping("/refresh")
    public ResponseBody<TokenResponseDTO> refresh(@RequestHeader("x-refresh-token") String refreshToken) {
        RefreshToken token = refreshTokenService.refresh(refreshToken);
        Authentication authentication = new UsernamePasswordAuthenticationToken(token.getUser().getUsername(), "");
        Jwt accessToken = accessTokenService.generate(authentication);

        return ResponseBody.<TokenResponseDTO>builder()
                .data(TokenResponseDTO.builder()
                        .accessToken(accessToken.getTokenValue())
                        .refreshToken(token.getToken())
                        .accessTokenExpiry(accessToken.getExpiresAt().toEpochMilli())
                        .refreshTokenExpiry(token.getExpiresAt().toInstant(ZoneOffset.UTC).toEpochMilli())
                        .build())
                .build();
    }
}
