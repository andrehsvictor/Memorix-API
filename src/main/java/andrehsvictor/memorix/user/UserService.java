package andrehsvictor.memorix.user;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.common.exception.BadRequestException;
import andrehsvictor.memorix.common.exception.ResourceConflictException;
import andrehsvictor.memorix.common.exception.ResourceNotFoundException;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.user.dto.CreateUserDto;
import andrehsvictor.memorix.user.dto.MeDto;
import andrehsvictor.memorix.user.dto.UpdateUserDto;
import andrehsvictor.memorix.user.dto.UserDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UserDto toUserDto(User user) {
        return userMapper.userToUserDto(user);
    }

    public MeDto toMeDto(User user) {
        return userMapper.userToMeDto(user);
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

    public User updateMe(UpdateUserDto updateUserDto) {
        User user = getById(jwtService.getCurrentUserUuid());
        if (updateUserDto.getUsername() != null && !updateUserDto.getUsername().equals(user.getUsername())
                && existsByUsername(updateUserDto.getUsername())) {
            throw new ResourceConflictException("Username already exists: " + updateUserDto.getUsername());
        }
        // Update user fields from DTO, verifying if username is unique
        // If the user changed the picture URL and the old one is from the Storage
        // Service,
        // we need to delete it, so we send a message using RabbitMQ
        // rabbitTemplate.convertAndSend(
        // "file-service.v1.delete",
        // user.getPictureUrl());
        throw new UnsupportedOperationException("UpdateMe method not implemented yet");
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

}
