package andrehsvictor.memorix.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Data Transfer Object for Google ID token authentication")
public class IdTokenDto {

    @NotBlank(message = "ID token must not be blank")
    @Schema(description = "Google ID token for authentication", example = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjE2NzAyN...")
    private String idToken;

}
