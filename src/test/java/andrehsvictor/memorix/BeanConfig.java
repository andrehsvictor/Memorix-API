package andrehsvictor.memorix;

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import andrehsvictor.memorix.email.EmailService;

@Configuration
public class BeanConfig {

    @Bean
    EmailService emailService() {
        return mock(EmailService.class);
    }

}
