package andrehsvictor.memorix.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.entity.ActivationCode;
import andrehsvictor.memorix.entity.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignupService {

    private final EmailService emailService;
    private final ActivationCodeService activationCodeService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public void signup(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.create(user);
        ActivationCode activationCode = activationCodeService.create(user);
        String to = user.getEmail();
        String subject = "Memorix - Activation Code";
        String body = "Your activation code is: " + activationCode.getCode();
        emailService.send(to, subject, body);
    }
}
