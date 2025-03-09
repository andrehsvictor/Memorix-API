package andrehsvictor.memorix.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordDto {

    @NotEmpty(message = "Old password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String oldPassword;

    @NotEmpty(message = "New password is required")
    @Pattern(message = "Password must be alphanumeric", regexp = "^[a-zA-Z0-9]+$")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String newPassword;

}
