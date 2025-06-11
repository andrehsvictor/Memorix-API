package andrehsvictor.memorix.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RevokeTokenDto {

    @NotBlank(message = "Token must not be blank")
    @Pattern(message = "Invalid JWT format", regexp = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$")
    private String token;

}
