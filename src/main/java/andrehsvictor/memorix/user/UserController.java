package andrehsvictor.memorix.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.user.dto.UserDto;
import andrehsvictor.memorix.util.StringUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/v1/users")
    public Page<UserDto> findAll(@RequestParam(required = false, name = "q") String query, Pageable pageable) {
        query = StringUtil.normalize(query);
        return userService.findAll(query, pageable).map(user -> userService.toDto(user));
    }

    @GetMapping("/api/v1/users/{id}")
    public UserDto findById(Long id) {
        return userService.toDto(userService.findById(id));
    }

    @GetMapping("/api/v1/users/me")
    public UserDto findMyself() {
        return userService.toDto(userService.findMyself());
    }

}
