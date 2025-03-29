package andrehsvictor.memorix.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendActionEmailDto {

    @Pattern(regexp = "^(RESET_PASSWORD|VERIFY_EMAIL)$", message = "Type must be either RESET_PASSWORD or VERIFY_EMAIL")
    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @NotBlank(message = "Redirect URL is required")
    @Pattern(regexp = "^(http|https)://.*$", message = "Redirect URL is invalid")
    private String redirectUrl;
}
