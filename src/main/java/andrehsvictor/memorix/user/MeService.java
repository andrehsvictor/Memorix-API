package andrehsvictor.memorix.user;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.common.exception.ResourceConflictException;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.user.dto.UpdateUserDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeService {

    private final UserService userService;
    private final JwtService jwtService;
    private final RabbitTemplate rabbitTemplate;

    public User updateMe(UpdateUserDto updateUserDto) {
        User user = userService.getById(jwtService.getCurrentUserUuid());
        if (updateUserDto.getUsername() != null && !updateUserDto.getUsername().equals(user.getUsername())
                && userService.existsByUsername(updateUserDto.getUsername())) {
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
}
