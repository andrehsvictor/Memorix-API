package andrehsvictor.memorix.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDto {

    @NotBlank(message = "Token is required")
    private String token;

    @Pattern(message = "New password must contain at least one letter and one number.", regexp = "^(?=.*[A-Za-z])(?=.*\\d).*$")
    @NotBlank(message = "New password is required")
    private String newPassword;

}