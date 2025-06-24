package andrehsvictor.memorix.user;

import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.common.exception.BadRequestException;
import andrehsvictor.memorix.common.exception.ResourceConflictException;
import andrehsvictor.memorix.common.exception.ResourceNotFoundException;
import andrehsvictor.memorix.user.dto.ChangeEmailDto;
import andrehsvictor.memorix.user.dto.CreateUserDto;
import andrehsvictor.memorix.user.dto.ResetPasswordDto;
import andrehsvictor.memorix.user.dto.SendActionEmailDto;
import andrehsvictor.memorix.user.dto.UserDto;
import andrehsvictor.memorix.user.dto.VerifyEmailDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;
    private final EmailVerifier emailVerifier;
    private final EmailChanger emailChanger;
    private final PasswordResetter passwordResetter;

    public UserDto toDto(User user) {
        return userMapper.userToUserDto(user);
    }

    public User create(CreateUserDto createUserDto) {
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new ResourceConflictException("User with email already exists: " + createUserDto.getEmail());
        }
        if (userRepository.existsByUsername(createUserDto.getUsername())) {
            throw new ResourceConflictException("User with username already exists: " + createUserDto.getUsername());
        }
        User user = userMapper.createUserDtoToUser(createUserDto);
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        return userRepository.save(user);
    }

    public Page<User> getAllWithFilters(
            String query,
            String username,
            String displayName,
            Pageable pageable) {
        return userRepository.findAllWithFilters(query, username, displayName, pageable);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    public User getByProviderId(String providerId) {
        return userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "provider ID", providerId));
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", id));
    }

    public User getByEmailVerificationToken(String token) {
        return userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid email verification token: " + token));
    }

    public User getByPasswordResetToken(String token) {
        return userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid password reset token: " + token));
    }

    public User getByEmailChangeToken(String token) {
        return userRepository.findByEmailChangeToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid email change token: " + token));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void sendActionEmail(SendActionEmailDto sendActionEmailDto) {
        switch (sendActionEmailDto.getAction()) {
            case VERIFY_EMAIL -> rabbitTemplate.convertAndSend("email-actions.v1.verify-email",
                    sendActionEmailDto);
            case RESET_PASSWORD -> rabbitTemplate.convertAndSend("email-actions.v1.reset-password",
                    sendActionEmailDto);
            default -> throw new BadRequestException("Invalid email action: " + sendActionEmailDto.getAction());
        }
    }

    public void verifyEmail(VerifyEmailDto verifyEmailDto) {
        emailVerifier.verifyEmail(verifyEmailDto.getToken());
    }

    public void changeEmail(ChangeEmailDto changeEmailDto) {
        emailChanger.changeEmail(changeEmailDto.getToken());
    }

    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        passwordResetter.resetPassword(resetPasswordDto.getToken(), resetPasswordDto.getPassword());
    }

    public void delete(UUID id) {
        userRepository.deleteById(id);
    }

}
