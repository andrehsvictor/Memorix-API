package andrehsvictor.memorix.user;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User save(User user) {
        if (user.getId() == null) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            return userRepository.save(user);
        }
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username + "."));
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id + "."));
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
