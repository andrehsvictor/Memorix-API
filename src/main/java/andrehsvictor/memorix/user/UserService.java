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
        String email = postUserDto.getEmail();
        if (existsByEmail(email)) {
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

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with e-mail '" + email + "'"));
    }

    public void verifyEmail(UUID id) {
        User user = getById(id);
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User update(UUID id, PutUserDto putUserDto) {
        String email = putUserDto.getEmail();
        User user = getById(id);
        boolean emailChanged = email != null && !email.equals(user.getEmail());
        if (emailChanged) {
            if (existsByEmail(email)) {
                throw new ResourceAlreadyExistsException("Username or e-mail already in use");
            }
        }
        user = userMapper.updateUserFromPutUserDto(putUserDto, user);
        return userRepository.save(user);
    }

    public boolean existsById(UUID id) {
        return userRepository.existsById(id);
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    private void encodePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

}
