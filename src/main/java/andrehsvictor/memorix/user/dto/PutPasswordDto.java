package andrehsvictor.memorix.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutPasswordDto {

    @NotBlank(message = "Password is required")
    @Pattern(message = "Password must contain at least one letter and one number.", regexp = "^(?=.*[A-Za-z])(?=.*\\d).*$")
    private String password;

}
