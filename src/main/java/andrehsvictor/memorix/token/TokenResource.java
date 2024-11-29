package andrehsvictor.memorix.token;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.token.dto.GetTokenDto;
import andrehsvictor.memorix.token.dto.PostTokenDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TokenResource {

    private final TokenFacade tokenFacade;

    @PostMapping("/auth/token")
    public ResponseEntity<GetTokenDto> request(@RequestBody PostTokenDto postTokenDto) {
        GetTokenDto getTokenDto = tokenFacade.issue(postTokenDto);
        return ResponseEntity.ok(getTokenDto);
    }
}
