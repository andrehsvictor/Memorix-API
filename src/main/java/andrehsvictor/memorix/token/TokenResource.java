package andrehsvictor.memorix.token;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.token.dto.GetTokenDto;
import andrehsvictor.memorix.token.dto.PostTokenDto;
import andrehsvictor.memorix.token.dto.RefreshTokenDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TokenResource {

    private final TokenFacade facade;

    @PostMapping("/auth/token")
    public ResponseEntity<GetTokenDto> getToken(@RequestBody @Valid PostTokenDto postTokenDto) {
        GetTokenDto getTokenDto = facade.getToken(postTokenDto);
        return ResponseEntity.ok(getTokenDto);
    }

    @PostMapping("/auth/token/refresh")
    public ResponseEntity<GetTokenDto> refreshToken(@RequestBody @Valid RefreshTokenDto refreshTokenDto) {
        GetTokenDto getTokenDto = facade.refreshToken(refreshTokenDto);
        return ResponseEntity.ok(getTokenDto);
    }
}
