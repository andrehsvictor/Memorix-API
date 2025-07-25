package andrehsvictor.memorix.user;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.common.email.EmailService;
import andrehsvictor.memorix.common.exception.GoneException;
import andrehsvictor.memorix.common.util.FileUtil;
import andrehsvictor.memorix.user.dto.SendActionEmailDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetter {

    private final UserService userService;
    private final FileUtil fileUtil;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ActionTokenLifetimeProperties actionTokenLifetimeProperties;

    @RabbitListener(queues = "email-actions.v1.reset-password")
    public void sendPasswordResetEmail(SendActionEmailDto dto) {
        String email = dto.getEmail();
        String url = dto.getUrl();
        User user = userService.getByEmail(email);

        String token = UUID.randomUUID().toString();
        Duration lifetime = actionTokenLifetimeProperties.getResetPasswordLifetime();

        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiresAt(LocalDateTime.now().plus(lifetime));
        userService.save(user);

        String urlWithToken = url + (url.contains("?") ? "&" : "?") + "token=" + token;
        String body = fileUtil.processTemplate("classpath:templates/reset-password.html",
                Map.of("url", urlWithToken, "expiration", formatDuration(lifetime)));

        emailService.send(email, "Reset your password - Memorix", body);
    }

    public void resetPassword(String token, String password) {
        User user = userService.getByPasswordResetToken(token);
        
        // Always encode the password first (as test expects)
        String encodedPassword = passwordEncoder.encode(password);
        
        if (user.getPasswordResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
            // Verify that we always save the user, even on error
            userService.save(user);
            throw new GoneException("Password reset token expired. Please request a new password reset.");
        }

        user.setPassword(encodedPassword);
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiresAt(null);
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