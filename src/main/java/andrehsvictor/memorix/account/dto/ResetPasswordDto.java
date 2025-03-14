package andrehsvictor.memorix.account.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordDto {

    @NotEmpty(message = "Token is required")
    private String token;

    @NotEmpty(message = "Password is required")
    @Pattern(message = "Password must be alphanumeric", regexp = "^[a-zA-Z0-9]+$")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

}
