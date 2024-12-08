package andrehsvictor.memorix.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutUserDto {

    @Size(min = 3, max = 50, message = "Display name must be between 1 and 50 characters")
    private String displayName;

    @Email(message = "E-mail must be valid")
    @Size(min = 5, max = 255, message = "E-mail must be between 5 and 255 characters")
    private String email;

    @Size(max = 255, message = "Bio must be at most 255 characters")
    private String bio;

    @Pattern(message = "Avatar URL must be a valid URL", regexp = "^(http|https)://.*$")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String avatarUrl;
}
