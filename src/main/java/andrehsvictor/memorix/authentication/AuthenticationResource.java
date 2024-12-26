package andrehsvictor.memorix.authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.authentication.dto.ActionEmailDto;
import andrehsvictor.memorix.authentication.dto.ResetPasswordDto;
import andrehsvictor.memorix.authentication.dto.VerifyEmailDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthenticationResource {

    private final AuthenticationService authenticationService;

    @PostMapping("/v1/auth/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestBody @Valid VerifyEmailDto verifyEmailDto) {
        authenticationService.verifyEmail(verifyEmailDto.getToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/v1/auth/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto) {
        authenticationService.resetPassword(resetPasswordDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/v1/auth/send-action-email")
    public ResponseEntity<Void> sendActionEmail(@RequestBody @Valid ActionEmailDto actionEmailDto) {
        authenticationService.sendActionEmail(actionEmailDto);
        return ResponseEntity.noContent().build();
    }

}
