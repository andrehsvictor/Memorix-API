package andrehsvictor.memorix.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActionEmailDto {

    @Email(message = "Invalid e-mail")
    @Size(min = 5, message = "E-mail must have at least 5 characters")
    private String email;

    @Pattern(regexp = "^(VERIFY_EMAIL|RESET_PASSWORD)$", message = "Action must be either VERIFY_EMAIL or RESET_PASSWORD")
    @NotBlank(message = "Action is required")
    private String action;

}
