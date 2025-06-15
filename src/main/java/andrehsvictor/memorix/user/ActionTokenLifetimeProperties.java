package andrehsvictor.memorix.user;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
public class ActionTokenLifetimeProperties {

    @Value("${memorix.action-tokens.verify-email.lifetime:30m}")
    private Duration verifyEmailLifetime = Duration.ofMinutes(30);

    @Value("${memorix.action-tokens.reset-password.lifetime:30m}")
    private Duration resetPasswordLifetime = Duration.ofMinutes(30);

    @Value("${memorix.action-tokens.change-email.lifetime:30m}")
    private Duration changeEmailLifetime = Duration.ofMinutes(30);

}
