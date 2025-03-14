package andrehsvictor.memorix.account;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.email.EmailService;
import andrehsvictor.memorix.exception.ResourceConflictException;
import andrehsvictor.memorix.file.FileService;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailService emailService;
    private final FileService fileService;
    private final UserService userService;
    private final VerificationTokenService verificationTokenService;

    public void sendVerificationEmail(String to, String redirectUrl) {
        if (userService.isEmailVerified(to)) {
            throw new ResourceConflictException("Email already verified");
        }
        User user = userService.findByEmail(to);
        String subject = "Verify your email";
        String text = fileService.read("classpath:verify-email.html");
        text = text.replace("{{url}}", redirectUrl);
        text = text.replace("{{token}}", verificationTokenService.generate(user.getId()));
        emailService.send(to, subject, text);
    }

    public void verify(String token) {
        Long userId = verificationTokenService.get(token);
        userService.setEmailVerified(userId, true);
        verificationTokenService.delete(token);
    }

}
