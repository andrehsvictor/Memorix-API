package andrehsvictor.memorix.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.user.dto.GetMeDto;
import andrehsvictor.memorix.user.dto.GetUserDto;
import andrehsvictor.memorix.user.dto.PostUserDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final UserMapper userMapper;
    private final RegistrationService registrationService;

    public Page<GetUserDto> findAll(Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        return users.map(userMapper::userToGetUserDto);
    }

    public GetUserDto findByUsername(String username) {
        User user = userService.findByUsername(username);
        return userMapper.userToGetUserDto(user);
    }

    public GetMeDto create(PostUserDto postUserDto) {
        User user = userMapper.postUserDtoToUser(postUserDto);
        user = registrationService.register(user);
        return userMapper.userToGetMeDto(user);
    }
}
