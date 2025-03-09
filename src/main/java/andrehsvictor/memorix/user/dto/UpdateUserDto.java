package andrehsvictor.memorix.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDto {

    @Pattern(message = "Username must contain only lowercase letters, numbers and underscores", regexp = "^[a-z0-9_]+$")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @Email(message = "Invalid email address")
    @Size(min = 5, max = 100, message = "Email must be between 5 and 100 characters")
    private String email;

    @Size(max = 100, message = "Display name must be less than 100 characters")
    private String displayName;

    @Pattern(message = "Invalid URL", regexp = "^(http|https)://.*$")
    private String pictureUrl;

    @Size(max = 500, message = "Bio must be less than 500 characters")
    private String bio;
}
