package andrehsvictor.memorix.user;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/users")
    public Page<GetUserDto> findAll(Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        return users.map(userMapper::userToGetUserDto);
    }

    @GetMapping("/users/{username}")
    public GetUserDto findByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return userMapper.userToGetUserDto(user);
    }

    @PostMapping("/users")
    public ResponseEntity<GetMeDto> create(@RequestBody @Valid PostUserDto postUserDto) {
        User user = userMapper.postUserDtoToUser(postUserDto);
        user = userService.save(user);
        GetMeDto getMeDto = userMapper.userToGetMeDto(user);
        URI location = URI.create("/users/" + user.getUsername());
        return ResponseEntity.created(location).body(getMeDto);
    }

}
