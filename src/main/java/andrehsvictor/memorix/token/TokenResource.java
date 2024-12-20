package andrehsvictor.memorix.token;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.token.dto.GetTokenDto;
import andrehsvictor.memorix.token.dto.PostTokenDto;
import andrehsvictor.memorix.token.dto.TokenDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TokenResource {

    private final TokenService tokenService;

    @PostMapping("/v1/auth/token")
    public GetTokenDto request(@RequestBody @Valid PostTokenDto postTokenDto) {
        return tokenService.get(postTokenDto);
    }

    @PostMapping("/v1/auth/token/refresh")
    public ResponseEntity<GetTokenDto> refresh(@RequestBody @Valid TokenDto tokenDto) {
        return ResponseEntity.ok(tokenService.refresh(tokenDto));
    }

    @PostMapping("/v1/auth/token/revoke")
    public ResponseEntity<Void> revoke(@RequestBody @Valid TokenDto tokenDto) {
        tokenService.revoke(tokenDto);
        return ResponseEntity.noContent().build();
    }
}
