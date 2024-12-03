package andrehsvictor.memorix.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.user.dto.GetMeDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/users/me")
    public ResponseEntity<GetMeDto> getMe(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userMapper.userToGetMeDto(user));
    }
}
