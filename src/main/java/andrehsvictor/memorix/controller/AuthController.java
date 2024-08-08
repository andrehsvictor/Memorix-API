package andrehsvictor.memorix.controller;

import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.dto.ResponseBody;
import andrehsvictor.memorix.dto.request.LoginRequestDTO;
import andrehsvictor.memorix.dto.request.SignupRequestDTO;
import andrehsvictor.memorix.dto.response.TokenResponseDTO;
import andrehsvictor.memorix.entity.RefreshToken;
import andrehsvictor.memorix.presenter.TokenPresenter;
import andrehsvictor.memorix.service.AccessTokenService;
import andrehsvictor.memorix.service.LoginService;
import andrehsvictor.memorix.service.RefreshTokenService;
import andrehsvictor.memorix.service.SignupService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

        private final LoginService loginService;
        private final SignupService signupService;
        private final AccessTokenService accessTokenService;
        private final RefreshTokenService refreshTokenService;
        private final TokenPresenter tokenPresenter;

        @PostMapping("/login")
        public ResponseBody<TokenResponseDTO> login(@RequestBody LoginRequestDTO request) {
                Authentication authentication = loginService.login(request.getUsernameOrEmail(), request.getPassword());
                Jwt accessToken = accessTokenService.generate(authentication);
                RefreshToken refreshToken = refreshTokenService.generate(authentication);

                return tokenPresenter.present(accessToken, refreshToken);
        }

        @PostMapping("/refresh")
        public ResponseBody<TokenResponseDTO> refresh(@RequestHeader("X-Refresh-Token") String refreshToken) {
                RefreshToken token = refreshTokenService.refresh(refreshToken);
                Authentication authentication = new UsernamePasswordAuthenticationToken(token.getUser().getUsername(),
                                "");
                Jwt accessToken = accessTokenService.generate(authentication);

                return tokenPresenter.present(accessToken, token);
        }

        @PostMapping("/signup")
        public ResponseBody<Map<String, String>> signup(@RequestBody SignupRequestDTO request) {
                signupService.signup(request.toUser());
                Map<String, String> response = Map.of("message",
                                "User registered successfully. Check your email for the activation code.");
                return ResponseBody.<Map<String, String>>builder().data(response).build();
        }
}
