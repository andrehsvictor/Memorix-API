package andrehsvictor.memorix.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "User authentication credentials")
public class CredentialsDto {

    @Schema(description = "Username or email address", example = "user@example.com")
    @NotBlank(message = "Username or email must not be blank")
    private String username; // or email

    @Schema(description = "User password", example = "MySecurePassword123!")
    @NotBlank(message = "Password must not be blank")
    private String password;
    
}
