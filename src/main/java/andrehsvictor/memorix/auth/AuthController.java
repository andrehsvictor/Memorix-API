package andrehsvictor.memorix.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.auth.dto.CredentialsDto;
import andrehsvictor.memorix.auth.dto.IdTokenDto;
import andrehsvictor.memorix.auth.dto.RefreshTokenDto;
import andrehsvictor.memorix.auth.dto.RevokeTokenDto;
import andrehsvictor.memorix.auth.dto.TokenDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;

    @PostMapping("/api/v1/auth/token")
    public TokenDto requestToken(@Valid @RequestBody CredentialsDto credentialsDto) {
        return tokenService.request(credentialsDto);
    }

    @PostMapping("/api/v1/auth/google")
    public TokenDto google(@Valid @RequestBody IdTokenDto idTokenDto) {
        return tokenService.google(idTokenDto);
    }

    @PostMapping("/api/v1/auth/refresh")
    public TokenDto refreshToken(@Valid @RequestBody RefreshTokenDto refreshTokenDto) {
        return tokenService.refresh(refreshTokenDto);
    }

    @PostMapping("/api/v1/auth/revoke")
    public ResponseEntity<Void> revokeToken(@Valid @RequestBody RevokeTokenDto revokeTokenDto) {
        tokenService.revoke(revokeTokenDto);
        return ResponseEntity.noContent().build();
    }

}
