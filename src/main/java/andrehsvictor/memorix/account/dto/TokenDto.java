package andrehsvictor.memorix.account.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TokenDto {

    @NotEmpty(message = "Token is required")
    private String token;

}
