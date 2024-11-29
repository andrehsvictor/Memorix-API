package andrehsvictor.memorix.token;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.authentication.AuthenticationService;
import andrehsvictor.memorix.token.dto.GetTokenDto;
import andrehsvictor.memorix.token.dto.PostTokenDto;
import andrehsvictor.memorix.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TokenResource {

    private final AuthenticationService authenticationService;
    private final TokenFacade tokenFacade;

    @PostMapping("/auth/token")
    public ResponseEntity<GetTokenDto> getToken(@RequestBody @Valid PostTokenDto postTokenDto) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                postTokenDto.getUsername(), postTokenDto.getPassword());
        Authentication authentication = authenticationService.authenticate(token);
        User user = (User) authentication.getPrincipal();
        GetTokenDto getTokenDto = tokenFacade.issue(user);
        return ResponseEntity.ok(getTokenDto);
    }
}
