package andrehsvictor.memorix.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserDto {

    @NotBlank(message = "Display name is required")
    @Size(max = 50, message = "Display name must be at most 50 characters long")
    private String displayName;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-z0-9_]+$", message = "Username must contain only lowercase letters, numbers, and underscores")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email must be a valid email address")
    private String email;

    @Pattern(regexp = "^(https?://)?([a-zA-Z0-9.-]+)(:[0-9]+)?(/.*)?$", message = "Picture URL must be a valid URL")
    @Size(max = 255, message = "Picture URL must be at most 255 characters long")
    private String pictureUrl;

    @Size(max = 500, message = "Bio must be at most 500 characters long")
    private String bio;
    
}
