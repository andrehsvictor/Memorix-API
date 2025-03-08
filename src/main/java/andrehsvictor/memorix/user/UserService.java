package andrehsvictor.memorix.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.jwt.JwtService;
import andrehsvictor.memorix.user.dto.CreateUserDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User create(CreateUserDto createUserDto) {
        User user = userMapper.createUserDtoToUser(createUserDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Page<User> findAll(String query, Pageable pageable) {
        if (query != null && !query.isEmpty()) {
            return userRepository.findAllByQuery(query, pageable);
        }
        return userRepository.findAll(pageable);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, "ID", id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, "email", email));
    }

    public User findMyself() {
        return findById(jwtService.getCurrentUserId());
    }

    public boolean isEmailVerified(String email) {
        return findByEmail(email).isEmailVerified();
    }

    public void setEmailVerified(String email, boolean verified) {
        User user = findByEmail(email);
        user.setEmailVerified(verified);
        userRepository.save(user);
    }
}
