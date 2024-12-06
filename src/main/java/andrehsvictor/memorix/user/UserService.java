package andrehsvictor.memorix.user;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.exception.ResourceAlreadyExistsException;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.user.dto.PostUserDto;
import andrehsvictor.memorix.user.dto.PutUserDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User create(PostUserDto postUserDto) {
        String username = postUserDto.getUsername();
        String email = postUserDto.getEmail();
        if (existsByUsernameOrEmail(username, email)) {
            throw new ResourceAlreadyExistsException("Username or e-mail already in use");
        }
        User user = userMapper.postUserDtoToUser(postUserDto);
        encodePassword(user);
        return userRepository.save(user);
    }

    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID '" + id + "'"));
    }

    public boolean existsByUsernameOrEmail(String username, String email) {
        return userRepository.existsByUsernameOrEmail(username, email);
    }

    public User update(PutUserDto putUserDto, User user) {
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
        return userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    private void encodePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

}
