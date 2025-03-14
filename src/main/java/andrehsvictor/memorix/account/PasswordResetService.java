package andrehsvictor.memorix.account;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.email.EmailService;
import andrehsvictor.memorix.file.FileService;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserService userService;
    private final ResetPasswordTokenService resetPasswordTokenService;
    private final EmailService emailService;
    private final FileService fileService;

    public void sendPasswordResetEmail(String to, String redirectUrl) {
        User user = userService.findByEmail(to);
        String subject = "Reset your password";
        String text = fileService.read("classpath:reset-password.html");
        text = text.replace("{{url}}", redirectUrl);
        text = text.replace("{{token}}", resetPasswordTokenService.generate(user.getId()));
        emailService.send(to, subject, text);
    }

    public void resetPassword(String token, String newPassword) {
        Long userId = resetPasswordTokenService.get(token);
        userService.setPassword(userId, newPassword);
        resetPasswordTokenService.delete(token);
    }

}
