package andrehsvictor.memorix.token.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TokenDto {

    @NotBlank(message = "Token is required")
    @Pattern(message = "Invalid JWT token", regexp = "^[a-zA-Z0-9-_=]+\\.[a-zA-Z0-9-_=]+\\.[a-zA-Z0-9-_.+/=]*$")
    private String token;

}
