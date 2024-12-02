package andrehsvictor.memorix.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUserDto {

    @Size(min = 1, max = 50, message = "Display name must be between 1 and 50 characters.")
    @NotBlank(message = "Display name is required.")
    private String displayName;

    @Size(min = 1, max = 50, message = "Username must be between 1 and 50 characters.")
    @NotBlank(message = "Username is required.")
    @Pattern(message = "Username must contain only letters, numbers, and underscores.", regexp = "^[a-zA-Z0-9_]*$")
    private String username;

    @NotBlank(message = "E-mail is required.")
    @Email(message = "E-mail must be valid.")
    @Size(min = 5, max = 255, message = "E-mail must be between 5 and 255 characters.")
    private String email;

    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters.")
    @Pattern(message = "Password must contain at least one letter and one number.", regexp = "^(?=.*[A-Za-z])(?=.*\\d).*$")
    private String password;

    @Size(max = 255, message = "Bio must be at most 255 characters.")
    private String bio;

    @Pattern(message = "Avatar URL must be a valid URL.", regexp = "^(http|https)://.*$")
    @Size(max = 255, message = "Avatar URL must be at most 255 characters.")
    private String avatarUrl;
}
