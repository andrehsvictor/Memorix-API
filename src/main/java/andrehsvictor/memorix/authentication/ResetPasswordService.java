package andrehsvictor.memorix.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.authentication.dto.ResetPasswordDto;
import andrehsvictor.memorix.email.EmailService;
import andrehsvictor.memorix.email.dto.EmailDto;
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
public class ResetPasswordService {

    private final EmailService emailService;
    private final FileService fileService;
    private final UserService userService;
    private final ActionTokenService actionTokenService;

    @Value("${memorix.frontend.reset-password.url:http://localhost:4200/reset-password}")
    private String resetPasswordUrl = "http://localhost:4200/reset-password";

    public void sendResetPasswordEmail(String email) {
        User user = userService.getByEmail(email);
        ActionToken actionToken = actionTokenService.issue(ActionType.RESET_PASSWORD, email);
        String text = fileService.read("classpath:email/reset-password.html");
        text = text.replace("{{name}}", user.getDisplayName());
        text = text.replace("{{resetUrl}}", resetPasswordUrl + "?token=" + actionToken.getToken());
        EmailDto emailDto = EmailDto.builder()
                .to(email)
                .subject("Memorix - Reset Password")
                .text(text)
                .build();
        emailService.send(emailDto);
    }

    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        ActionToken actionToken = actionTokenService.get(resetPasswordDto.getToken());
        if (!actionTokenService.isValid(resetPasswordDto.getToken())
                || !actionToken.getAction().equals(ActionType.RESET_PASSWORD)) {
            throw new UnauthorizedException("Invalid or expired token");
        }
        User user = userService.getByEmail(actionToken.getEmail());
        userService.updatePassword(user, resetPasswordDto.getNewPassword());
        actionTokenService.delete(resetPasswordDto.getToken());
    }
}
