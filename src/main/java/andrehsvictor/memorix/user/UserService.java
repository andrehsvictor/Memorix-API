package andrehsvictor.memorix.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.user.dto.UserDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Page<UserDto> getAllDto(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::userToUserDto);
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(User.class, "ID", id));
    }

    public UserDto getDtoById(Long id) {
        return userMapper.userToUserDto(getById(id));
    }

    public Page<UserDto> getDtosByQuery(String query, Pageable pageable) {
        Page<User> users = userRepository.findAllByQuery(query, pageable);
        return users.map(userMapper::userToUserDto);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, "email", email));
    }

    public boolean isEmailVerified(String email) {
        return getByEmail(email).isEmailVerified();
    }

    public void setEmailVerified(String email, boolean verified) {
        User user = getByEmail(email);
        user.setEmailVerified(verified);
        userRepository.save(user);
    }
}
