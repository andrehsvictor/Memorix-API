package andrehsvictor.memorix.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.exception.BadRequestException;
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

    public User create(CreateUserDto createUserDto) {
        User user = userMapper.createUserDtoToUser(createUserDto);
        setPassword(user, createUserDto.getPassword());
        return userRepository.save(user);
    }

    public Page<User> findAll(String query, Pageable pageable) {
        return userRepository.findAll(query, pageable);
    }

    public User update(Long id, UpdateUserDto updateUserDto) {
        User user = findById(id);
        userMapper.updateUserFromUpdateUserDto(updateUserDto, user);
        return userRepository.save(user);
    }

    public void updatePassword(Long id, UpdatePasswordDto updatePasswordDto) {
        User user = findById(id);
        if (!matchesPassword(updatePasswordDto.getOldPassword(), user.getPassword())) {
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

    private void setPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
    }

    private boolean matchesPassword(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }
}
