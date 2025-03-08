package andrehsvictor.memorix.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SendActionEmailDto {

    @Pattern(regexp = "^(RESET_PASSWORD|VERIFY_EMAIL)$", message = "Type is invalid")
    @NotEmpty(message = "Type is required")
    private String type;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @NotEmpty(message = "Redirect URL is required")
    @Pattern(regexp = "^(http|https)://.*$", message = "Redirect URL is invalid")
    private String redirectUrl;
}
