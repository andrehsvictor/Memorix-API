package andrehsvictor.memorix.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import andrehsvictor.memorix.exception.BadRequestException;
import andrehsvictor.memorix.exception.ResourceConflictException;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.jwt.JwtService;
import andrehsvictor.memorix.user.dto.CreateUserDto;
import andrehsvictor.memorix.user.dto.UpdatePasswordDto;
import andrehsvictor.memorix.user.dto.UpdateUserDto;
import andrehsvictor.memorix.user.dto.UserDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserDto toDto(User user) {
        return userMapper.userToUserDto(user);
    }

    @Transactional
    public User create(CreateUserDto createUserDto) {
        User user = userMapper.createUserDtoToUser(createUserDto);
        boolean emailExists = existsByEmail(createUserDto.getEmail());
        boolean usernameExists = existsByUsername(createUserDto.getUsername());
        if (emailExists || usernameExists) {
            throw new ResourceConflictException("Username or email already in use");
        }
        setPassword(user, createUserDto.getPassword());
        return userRepository.save(user);
    }

    public Page<User> findAll(String query, Pageable pageable) {
        query = query != null ? query.trim() : null;
        return userRepository.findAll(query, pageable);
    }

    @Transactional
    public User update(Long id, UpdateUserDto updateUserDto) {
        User user = findById(id);
        boolean emailExists = updateUserDto.getEmail() != null && existsByEmail(updateUserDto.getEmail());
        boolean usernameExists = updateUserDto.getUsername() != null && existsByUsername(updateUserDto.getUsername());
        boolean emailChanged = updateUserDto.getEmail() != null && !updateUserDto.getEmail().equals(user.getEmail());
        if (emailExists || usernameExists) {
            throw new ResourceConflictException("Username or email already in use");
        }
        if (emailChanged) {
            user.setEmailVerified(false);
        }
        userMapper.updateUserFromUpdateUserDto(updateUserDto, user);
        return userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional
    public void updatePassword(Long id, UpdatePasswordDto updatePasswordDto) {
        User user = findById(id);
        boolean matches = matchesPassword(updatePasswordDto.getOldPassword(), user.getPassword());
        if (!matches) {
            throw new BadRequestException("Old password is incorrect");
        }
        setPassword(user, updatePasswordDto.getNewPassword());
        userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, "ID", id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, "email", email));
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public User findMyself() {
        return findById(jwtService.getCurrentUserId());
    }

    public boolean isEmailVerified(String email) {
        return findByEmail(email).isEmailVerified();
    }

    @Transactional
    public void setEmailVerified(Long id, boolean verified) {
        User user = findById(id);
        user.setEmailVerified(verified);
        userRepository.save(user);
    }

    @Transactional
    public void setPassword(Long id, String password) {
        User user = findById(id);
        setPassword(user, password);
        userRepository.save(user);
    }

    private void setPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
    }

    private boolean matchesPassword(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }
}
