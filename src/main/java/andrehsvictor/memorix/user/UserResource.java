package andrehsvictor.memorix.user;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.user.dto.GetMeDto;
import andrehsvictor.memorix.user.dto.GetUserDto;
import andrehsvictor.memorix.user.dto.PostUserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/v1/users/me")
    public ResponseEntity<GetMeDto> getMe(@AuthenticationPrincipal User user) {
        GetMeDto getMeDto = userMapper.userToGetMeDto(user);
        return ResponseEntity.ok(getMeDto);
    }

    @PostMapping("/v1/users")
    public ResponseEntity<GetMeDto> create(@RequestBody @Valid PostUserDto postUserDto) {
        User user = userMapper.postUserDtoToUser(postUserDto);
        user = userService.save(user);
        GetMeDto getMeDto = userMapper.userToGetMeDto(user);
        URI location = URI.create("/v1/users/" + user.getUsername());
        return ResponseEntity.created(location).body(getMeDto);
    }

    @GetMapping("/v1/users")
    public Page<GetUserDto> findAll(@Valid UserQuery userQuery, Pageable pageable) {
        Page<User> users;
        if (userQuery.getDisplayName() != null || userQuery.getUsername() != null) {
            users = userService.searchByDisplayNameOrUsername(userQuery.getDisplayName(), userQuery.getUsername(),
                    pageable);
        } else {
            users = userService.findAll(pageable);
        }
        return users.map(userMapper::userToGetUserDto);
    }

    @GetMapping("/v1/users/{username}")
    public ResponseEntity<GetUserDto> findByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        GetUserDto getUserDto = userMapper.userToGetUserDto(user);
        return ResponseEntity.ok(getUserDto);
    }
}
