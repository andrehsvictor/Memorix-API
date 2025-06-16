package andrehsvictor.memorix.user;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.common.email.EmailService;
import andrehsvictor.memorix.common.exception.GoneException;
import andrehsvictor.memorix.common.exception.ResourceConflictException;
import andrehsvictor.memorix.common.util.FileUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailVerifier {

    private final UserService userService;
    private final FileUtil fileUtil;
    private final EmailService emailService;
    private final ActionTokenLifetimeProperties actionTokenLifetimeProperties;

    @RabbitListener(queues = "email-actions.v1.verify-email")
    public void sendVerificationEmail(String url, String email) {
        User user = userService.getByEmail(email);
        if (user.isEmailVerified()) {
            throw new ResourceConflictException("Email already verified: " + email);
        }

        String token = UUID.randomUUID().toString();
        Duration lifetime = actionTokenLifetimeProperties.getVerifyEmailLifetime();

        user.setEmailVerificationToken(token);
        user.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plus(lifetime));
        userService.save(user);

        String urlWithToken = url + (url.contains("?") ? "&" : "?") + "token=" + token;
        String body = fileUtil.processTemplate("classpath:templates/verify-email.html",
                Map.of("url", urlWithToken, "expiration", formatDuration(lifetime)));

        emailService.send(email, "Verify your email address - Memorix", body);
    }

    public void verifyEmail(String token) {
        User user = userService.getByEmailVerificationToken(token);

        if (user.getEmailVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new GoneException("Action token expired. Please request a new verification email.");
        }

        if (user.isEmailVerified()) {
            throw new ResourceConflictException("Email already verified: " + user.getEmail());
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiresAt(null);
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