package andrehsvictor.memorix.user;

import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.common.exception.BadRequestException;
import andrehsvictor.memorix.common.exception.ResourceConflictException;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.user.dto.MeDto;
import andrehsvictor.memorix.user.dto.UpdatePasswordDto;
import andrehsvictor.memorix.user.dto.UpdateUserDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeService {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;

    public MeDto toDto(User user) {
        return userMapper.userToMeDto(user);
    }

    public void updatePassword(UpdatePasswordDto updatePasswordDto) {
        UUID userId = jwtService.getCurrentUserUuid();
        User user = userService.getById(userId);
        String oldPassword = updatePasswordDto.getOldPassword();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        String newPassword = updatePasswordDto.getNewPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);
    }

    public User getMe() {
        UUID userId = jwtService.getCurrentUserUuid();
        return userService.getById(userId);
    }

    public User updateMe(UpdateUserDto updateUserDto) {
        User user = userService.getById(jwtService.getCurrentUserUuid());
        if (updateUserDto.getUsername() != null && !updateUserDto.getUsername().equals(user.getUsername())
                && userService.existsByUsername(updateUserDto.getUsername())) {
            throw new ResourceConflictException("Username already exists: " + updateUserDto.getUsername());
        }
        boolean pictureChanged = updateUserDto.getPictureUrl() != null
                && !updateUserDto.getPictureUrl().equals(user.getPictureUrl());
        String oldPictureUrl = user.getPictureUrl();
        userMapper.updateUserFromUpdateUserDto(updateUserDto, user);
        userService.save(user);
        if (pictureChanged && oldPictureUrl != null) {
            rabbitTemplate.convertAndSend(
                    "minio.v1.delete.url",
                    oldPictureUrl);
        }
        return user;
    }

    public void deleteMe() {
        User user = userService.getById(jwtService.getCurrentUserUuid());
        UUID userId = user.getId();
        userService.delete(userId);
        rabbitTemplate.convertAndSend(
                "minio.v1.delete.metadata",
                Map.of("userId", userId.toString()));
        // Must delete all cards and reviews associated with the user in MongoDB
        throw new UnsupportedOperationException("Delete user is not implemented yet");
    }
}
