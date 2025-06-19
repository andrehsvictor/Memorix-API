package andrehsvictor.memorix.user;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.user.dto.ChangeEmailDto;
import andrehsvictor.memorix.user.dto.MeDto;
import andrehsvictor.memorix.user.dto.ResetPasswordDto;
import andrehsvictor.memorix.user.dto.SendActionEmailDto;
import andrehsvictor.memorix.user.dto.UpdatePasswordDto;
import andrehsvictor.memorix.user.dto.UpdateUserDto;
import andrehsvictor.memorix.user.dto.UserDto;
import andrehsvictor.memorix.user.dto.VerifyEmailDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MeService meService;

    @GetMapping("/api/v1/users/me")
    public MeDto getMe() {
        User user = meService.getMe();
        return meService.toDto(user);
    }

    @GetMapping("/api/v1/users")
    public Page<UserDto> getAll(
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String displayName,
            Pageable pageable) {
        return userService.getAllWithFilters(query, username, displayName, pageable)
                .map(userService::toDto);
    }

    @GetMapping("/api/v1/users/{id}")
    public UserDto getById(@RequestParam UUID id) {
        User user = userService.getById(id);
        return userService.toDto(user);
    }

    @PostMapping("/api/v1/users/send-action-email")
    public ResponseEntity<Void> sendActionEmail(@Valid @RequestBody SendActionEmailDto dto) {
        userService.sendActionEmail(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/v1/users/verify-email")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody VerifyEmailDto dto) {
        userService.verifyEmail(dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/v1/users/email")
    public ResponseEntity<Void> changeEmail(@Valid @RequestBody ChangeEmailDto dto) {
        userService.changeEmail(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/v1/users/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordDto dto) {
        userService.resetPassword(dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/v1/users/me/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UpdatePasswordDto dto) {
        meService.updatePassword(dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/v1/users/me")
    public MeDto updateMe(@Valid @RequestBody UpdateUserDto dto) {
        User user = meService.updateMe(dto);
        return meService.toDto(user);
    }

    @DeleteMapping("/api/v1/users/me")
    public ResponseEntity<Void> deleteMe() {
        meService.deleteMe();
        return ResponseEntity.noContent().build();
    }

}
