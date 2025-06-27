package andrehsvictor.memorix.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Data Transfer Object representing a user")
public class UserDto {
    @Schema(description = "Unique identifier of the user", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;
    
    @Schema(description = "Unique username", example = "john_doe")
    private String username;
    
    @Schema(description = "Display name of the user", example = "John Doe")
    private String displayName;
    
    @Schema(description = "Profile picture URL", example = "https://example.com/avatar.jpg")
    private String pictureUrl;
    
    @Schema(description = "User biography or description", example = "Language learner and flashcard enthusiast")
    private String bio;
    
    @Schema(description = "Account creation timestamp in ISO format", example = "2025-06-27T10:30:00Z")
    private String createdAt;
}
