package andrehsvictor.memorix.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.exception.ResourceAlreadyExistsException;
import andrehsvictor.memorix.user.dto.GetUserDto;
import andrehsvictor.memorix.user.dto.PostUserDto;
import andrehsvictor.memorix.user.dto.PutUserDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public GetUserDto create(PostUserDto postUserDto) {
        String username = postUserDto.getUsername();
        String email = postUserDto.getEmail();
        if (existsByUsernameOrEmail(username, email)) {
            throw new ResourceAlreadyExistsException("Username or e-mail already in use");
        }
        User user = userMapper.postUserDtoToUser(postUserDto);
        encodePassword(user);
        userRepository.save(user);
        return userMapper.userToGetUserDto(user);
    }

    public GetUserDto get(User user) {
        return userMapper.userToGetUserDto(user);
    }

    public boolean existsByUsernameOrEmail(String username, String email) {
        return userRepository.existsByUsernameOrEmail(username, email);
    }

    public GetUserDto update(PutUserDto putUserDto, User user) {
        String email = putUserDto.getEmail();
        String username = putUserDto.getUsername();
        boolean emailChanged = email != null && !email.equals(user.getEmail());
        boolean usernameChanged = username != null && !username.equals(user.getUsername());
        if (emailChanged || usernameChanged) {
            if (existsByUsernameOrEmail(username, email)) {
                throw new ResourceAlreadyExistsException("Username or e-mail already in use");
            }
        }
        user = userMapper.updateUserFromPutUserDto(putUserDto, user);
        userRepository.save(user);
        return userMapper.userToGetUserDto(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    private void encodePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

}
