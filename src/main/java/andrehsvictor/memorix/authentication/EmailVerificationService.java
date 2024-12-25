package andrehsvictor.memorix.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.email.EmailService;
import andrehsvictor.memorix.email.dto.EmailDto;
import andrehsvictor.memorix.exception.ResourceAlreadyExistsException;
import andrehsvictor.memorix.exception.UnauthorizedException;
import andrehsvictor.memorix.file.FileService;
import andrehsvictor.memorix.token.actiontoken.ActionToken;
import andrehsvictor.memorix.token.actiontoken.ActionTokenService;
import andrehsvictor.memorix.token.actiontoken.ActionType;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailService emailService;
    private final ActionTokenService actionTokenService;
    private final FileService fileService;
    private final UserService userService;

    @Value("${memorix.frontend.verify-email.url:http://localhost:4200/verify-email}")
    private String verifyEmailUrl = "http://localhost:4200/verify-email";

    public void sendVerificationEmail(String email) {
        User user = userService.getByEmail(email);
        if (user.isEmailVerified()) {
            throw new ResourceAlreadyExistsException("E-mail already verified");
        }
        String token = actionTokenService.issue(ActionType.VERIFY_EMAIL, email).getToken();
        String text = fileService.importFileAsText("classpath:email/verify-email.html");
        EmailDto emailDto = EmailDto.builder()
                .to(email)
                .text(text.replace("{{name}}", user.getDisplayName())
                        .replace("{{url}}", verifyEmailUrl + "?token=" + token))
                .subject("Memorix - E-mail verification")
                .build();
        emailService.send(emailDto);
    }

    public void verifyEmail(String token) {
        ActionToken actionToken = actionTokenService.get(token);
        System.out.println(actionToken);
        if (!actionTokenService.isValid(token) || !actionToken.getAction().equals(ActionType.VERIFY_EMAIL)) {
            throw new UnauthorizedException("Invalid or expired token");
        }
        String email = actionToken.getEmail();
        User user = userService.getByEmail(email);
        userService.verifyEmail(user.getId());
        actionTokenService.delete(token);
    }
}
