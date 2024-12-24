package andrehsvictor.memorix.authentication;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.authentication.dto.PostEmailDto;
import andrehsvictor.memorix.token.dto.TokenDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthenticationResource {

    private final AuthenticationService authenticationService;

    @PostMapping("/v1/auth/send-verification-email")
    public Map<String, String> sendVerificationEmail(@RequestBody @Valid PostEmailDto postEmailDto) {
        authenticationService.sendVerificationEmail(postEmailDto.getEmail());
        return Map.of("message", "Verification e-mail sent");
    }

    @PostMapping("/v1/auth/verify-email")
    public Map<String, String> verifyEmail(@RequestBody @Valid TokenDto tokenDto) {
        authenticationService.verifyEmail(tokenDto.getToken());
        return Map.of("message", "E-mail verified successfully");
    }
}
