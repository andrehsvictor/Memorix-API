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

    private final TokenFacade facade;

    @PostMapping("/auth/token")
    public ResponseEntity<GetTokenDto> request(@RequestBody @Valid PostTokenDto postTokenDto) {
        GetTokenDto getTokenDto = facade.request(postTokenDto);
        return ResponseEntity.ok(getTokenDto);
    }

    @PostMapping("/auth/token/refresh")
    public ResponseEntity<GetTokenDto> refresh(@RequestBody @Valid TokenDto tokenDto) {
        GetTokenDto getTokenDto = facade.refresh(tokenDto);
        return ResponseEntity.ok(getTokenDto);
    }

    @PostMapping("/auth/token/revoke")
    public ResponseEntity<Void> revoke(@RequestBody @Valid TokenDto tokenDto) {
        facade.revoke(tokenDto);
        return ResponseEntity.noContent().build();
    }
}
