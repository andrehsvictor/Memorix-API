package andrehsvictor.memorix.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyEmailDto {

    @NotBlank(message = "Token is required")
    private String token;
    
}
