package andrehsvictor.memorix.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.entity.ActivationCode;
import andrehsvictor.memorix.entity.User;
import andrehsvictor.memorix.exception.MemorixException;
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

    public void activate(String code) {
        activationCodeService.validate(code);
        ActivationCode activationCode = activationCodeService.findByCode(code);
        User user = activationCode.getUser();
        userService.activate(user);
        activationCodeService.delete(activationCode);
    }

    public void resendActivationCode(String email) {
        User user = userService.findByUsernameOrEmail(email);
        checkUserAlreadyActivated(user);
        checkActivationCodeNotExpired(user);
        ActivationCode activationCode = activationCodeService.create(user);
        String to = user.getEmail();
        String subject = "Memorix - Activation Code";
        String body = "Your activation code is: " + activationCode.getCode();
        emailService.send(to, subject, body);
    }

    private void checkActivationCodeNotExpired(User user) {
        ActivationCode activationCode = user.getActivationCode();
        if (!activationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new MemorixException(HttpStatus.BAD_REQUEST, "Activation code is not expired yet. Please wait for the current activation code to expire.");
        }
    }

    private void checkUserAlreadyActivated(User user) {
        if (user.isEnabled()) {
            throw new MemorixException(HttpStatus.BAD_REQUEST, "User is already activated.");
        }
    }
}
