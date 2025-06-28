package andrehsvictor.memorix.user;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.common.email.EmailService;
import andrehsvictor.memorix.common.exception.BadRequestException;
import andrehsvictor.memorix.common.exception.GoneException;
import andrehsvictor.memorix.common.exception.ResourceConflictException;
import andrehsvictor.memorix.common.util.FileUtil;
import andrehsvictor.memorix.user.dto.EmailChangeDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailChanger {

    private final UserService userService;
    private final FileUtil fileUtil;
    private final EmailService emailService;
    private final ActionTokenLifetimeProperties actionTokenLifetimeProperties;

    @RabbitListener(queues = "email-actions.v1.change-email")
    public void sendEmailChangeRequest(EmailChangeDto dto) {
        String email = dto.getEmail();
        String url = dto.getUrl();
        UUID userId = dto.getUserId();

        User user = userService.getById(userId);

        if (user.getProvider() != UserProvider.LOCAL) {
            throw new BadRequestException(
                    "Email change is only allowed for local accounts. Current provider: " + user.getProvider());
        }

        if (userService.existsByEmail(email)) {
            throw new ResourceConflictException("Email already in use: " + email);
        }

        String token = UUID.randomUUID().toString();
        Duration lifetime = actionTokenLifetimeProperties.getChangeEmailLifetime();

        user.setEmailChange(email);
        user.setEmailChangeToken(token);
        user.setEmailChangeTokenExpiresAt(LocalDateTime.now().plus(lifetime));
        userService.save(user);

        String urlWithToken = url + (url.contains("?") ? "&" : "?") + "token=" + token;
        String body = fileUtil.processTemplate("classpath:templates/change-email.html",
                Map.of("url", urlWithToken, "expiration", formatDuration(lifetime)));

        emailService.send(email, "Confirm your email change - Memorix", body);
    }

    public void changeEmail(String token) {
        User user = userService.getByEmailChangeToken(token);

        if (user.getEmailChangeTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new GoneException("Email change token expired. Please request a new email change.");
        }

        if (userService.existsByEmail(user.getEmailChange())) {
            throw new ResourceConflictException("Email already in use: " + user.getEmailChange());
        }

        user.setEmail(user.getEmailChange());
        user.setEmailVerified(true);
        user.setEmailChange(null);
        user.setEmailChangeToken(null);
        user.setEmailChangeTokenExpiresAt(null);
        userService.save(user);
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        if (hours > 0) {
            return hours + " hour" + (hours != 1 ? "s" : "");
        }
        long minutes = duration.toMinutes();
        return minutes + " minute" + (minutes != 1 ? "s" : "");
    }
}