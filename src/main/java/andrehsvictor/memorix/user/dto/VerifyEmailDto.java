package andrehsvictor.memorix.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyEmailDto {

    @NotBlank(message = "Token cannot be blank")
    private String token;
    
}
