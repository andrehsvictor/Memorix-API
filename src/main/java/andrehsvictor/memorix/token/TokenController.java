package andrehsvictor.memorix.token;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.token.dto.AccessTokenDto;
import andrehsvictor.memorix.token.dto.CredentialsDto;
import andrehsvictor.memorix.token.dto.TokenDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/api/v1/token")
    public AccessTokenDto request(@RequestBody @Valid CredentialsDto credentials) {
        return tokenService.request(credentials);
    }

    @PostMapping("/api/v1/token/refresh")
    public AccessTokenDto refresh(@RequestBody @Valid TokenDto tokenDto) {
        return tokenService.refresh(tokenDto);
    }

    @DeleteMapping("/api/v1/token")
    public ResponseEntity<?> revoke(@RequestBody @Valid TokenDto tokenDto) {
        tokenService.revoke(tokenDto);
        return ResponseEntity.noContent().build();
    }
}
