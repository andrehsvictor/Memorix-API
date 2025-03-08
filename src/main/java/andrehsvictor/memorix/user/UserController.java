package andrehsvictor.memorix.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.user.dto.UserDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/api/v1/users")
    public Page<UserDto> findAll(@RequestParam(required = false, name = "q") String query, Pageable pageable) {
        return userService.findAll(query, pageable).map(userMapper::userToUserDto);
    }

    @GetMapping("/api/v1/users/{id}")
    public UserDto findById(Long id) {
        return userMapper.userToUserDto(userService.findById(id));
    }

    @GetMapping("/api/v1/users/me")
    public UserDto findMyself() {
        return userMapper.userToUserDto(userService.findMyself());
    }

}
