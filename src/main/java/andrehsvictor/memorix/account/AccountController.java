package andrehsvictor.memorix.account;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.account.dto.AccountDto;
import andrehsvictor.memorix.account.dto.ResetPasswordDto;
import andrehsvictor.memorix.account.dto.SendActionEmailDto;
import andrehsvictor.memorix.account.dto.TokenDto;
import andrehsvictor.memorix.user.dto.CreateUserDto;
import andrehsvictor.memorix.user.dto.UpdatePasswordDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/api/v1/account")
    public ResponseEntity<AccountDto> create(@RequestBody @Valid CreateUserDto createUserDto) {
        URI location = URI.create("/api/v1/account");
        return ResponseEntity.created(location).body(accountService.create(createUserDto));
    }

    @PostMapping("/api/v1/account/send-action-email")
    public ResponseEntity<Void> sendVerificationEmail(
            @RequestBody @Valid SendActionEmailDto sendActionEmailDto) {
        accountService.sendActionEmail(sendActionEmailDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/v1/account/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid UpdatePasswordDto updatePasswordDto) {
        accountService.updatePassword(updatePasswordDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/v1/account/verify")
    public ResponseEntity<Void> verify(@RequestBody TokenDto tokenDto) {
        accountService.verify(tokenDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/v1/account/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        accountService.resetPassword(resetPasswordDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/v1/account")
    public ResponseEntity<AccountDto> get() {
        return ResponseEntity.ok(accountService.get());
    }

    @DeleteMapping("/api/v1/account")
    public ResponseEntity<Void> delete() {
        accountService.delete();
        return ResponseEntity.noContent().build();
    }

}
