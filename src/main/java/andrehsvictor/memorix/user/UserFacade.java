package andrehsvictor.memorix.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.exception.ResourceAlreadyExistsException;
import andrehsvictor.memorix.user.dto.GetMeDto;
import andrehsvictor.memorix.user.dto.GetUserDto;
import andrehsvictor.memorix.user.dto.PostUserDto;
import andrehsvictor.memorix.user.dto.PutUserDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final UserMapper userMapper;

    public GetMeDto create(PostUserDto postUserDto) {
        if (userService.existsByUsernameOrEmail(postUserDto.getUsername(), postUserDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Username or e-mail already in use");
        }
        User user = userMapper.postUserDtoToUser(postUserDto);
        user = userService.save(user);
        return userMapper.userToGetMeDto(user);
    }

    public GetMeDto getMe(User user) {
        return userMapper.userToGetMeDto(user);
    }

    public GetMeDto updateMe(User user, PutUserDto putUserDto) {
        String username = putUserDto.getUsername();
        String email = putUserDto.getEmail();
        boolean usernameChanged = username != null && !username.equals(user.getUsername());
        boolean emailChanged = email != null && !email.equals(user.getEmail());
        if (usernameChanged || emailChanged) {
            if (userService.existsByUsernameOrEmail(username, email)) {
                throw new ResourceAlreadyExistsException("Username or e-mail already in use");
            }
        }
        user = userMapper.updateUserFromPutUserDto(putUserDto, user);
        user = userService.save(user);
        return userMapper.userToGetMeDto(user);
    }

    public void deleteMe(User user) {
        userService.deleteById(user.getId());
    }

    public GetUserDto findByUsername(String username) {
        User user = userService.findByUsername(username);
        return userMapper.userToGetUserDto(user);
    }

    public Page<GetUserDto> searchByDisplayNameOrUsername(String displayName, String username, Pageable pageable) {
        return userService.searchByDisplayNameOrUsername(displayName, username, pageable)
                .map(userMapper::userToGetUserDto);
    }

    public Page<GetUserDto> findAll(Pageable pageable) {
        return userService.findAll(pageable).map(userMapper::userToGetUserDto);
    }
}
