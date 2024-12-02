package andrehsvictor.memorix.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.email.Email;
import andrehsvictor.memorix.email.EmailService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public User register(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = userService.save(user);
        Email email = Email.builder()
                .to(savedUser.getEmail())
                .subject("Verify your e-mail address")
                .body("Welcome to Memorix, " + savedUser.getUsername() + "!")
                .build();
        emailService.send(email);
        return savedUser;
    }
}
