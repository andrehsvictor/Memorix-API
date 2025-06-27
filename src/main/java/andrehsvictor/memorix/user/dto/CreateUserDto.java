package andrehsvictor.memorix.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Data Transfer Object for creating a new user account")
public class CreateUserDto {

    @NotBlank(message = "Display name is required")
    @Size(max = 50, message = "Display name must be at most 50 characters long")
    @Schema(description = "User's display name", example = "John Doe", maxLength = 50)
    private String displayName;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-z0-9_]+$", message = "Username must contain only lowercase letters, numbers, and underscores")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long")
    @Schema(description = "Unique username (lowercase letters, numbers, underscores only)", example = "john_doe", minLength = 3, maxLength = 20)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters long")
    @Schema(description = "User password (minimum 8 characters)", example = "mySecurePassword123", minLength = 8, maxLength = 100)
    private String password;

    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email must be a valid email address")
    @Schema(description = "Valid email address", example = "john.doe@example.com")
    private String email;

    @Pattern(regexp = "^(https?://)?([a-zA-Z0-9.-]+)(:[0-9]+)?(/.*)?$", message = "Picture URL must be a valid URL")
    @Size(max = 255, message = "Picture URL must be at most 255 characters long")
    @Schema(description = "Optional profile picture URL", example = "https://example.com/avatar.jpg", maxLength = 255)
    private String pictureUrl;

    @Size(max = 500, message = "Bio must be at most 500 characters long")
    @Schema(description = "Optional user biography", example = "Language learner and flashcard enthusiast", maxLength = 500)
    private String bio;
    
}
