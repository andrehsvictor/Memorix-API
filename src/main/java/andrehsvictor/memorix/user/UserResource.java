package andrehsvictor.memorix.user;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.user.dto.GetUserDto;
import andrehsvictor.memorix.user.dto.PostUserDto;
import andrehsvictor.memorix.user.dto.PutUserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/v1/users")
    @ResponseStatus(code = HttpStatus.CREATED)
    public GetUserDto create(@RequestBody @Valid PostUserDto postUserDto) {
        User user = userService.create(postUserDto);
        return userMapper.userToGetUserDto(user);
    }

    @GetMapping("/v1/users/me")
    public GetUserDto getMe(@AuthenticationPrincipal User user) {
        return userMapper.userToGetUserDto(user);
    }

    @PutMapping("/v1/users/me")
    public GetUserDto updateMe(@RequestBody @Valid PutUserDto putUserDto, @AuthenticationPrincipal User user) {
        user = userService.update(putUserDto, user);
        return userMapper.userToGetUserDto(user);
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("/v1/users/me")
    public void deleteMe(@AuthenticationPrincipal User user) {
        userService.delete(user);
    }
}
