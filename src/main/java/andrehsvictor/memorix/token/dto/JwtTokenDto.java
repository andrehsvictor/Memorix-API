package andrehsvictor.memorix.token.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtTokenDto {

    @Pattern(message = "Invalid JWT token", regexp = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$")
    @NotBlank(message = "A JWT token is required")
    private String token;

}
