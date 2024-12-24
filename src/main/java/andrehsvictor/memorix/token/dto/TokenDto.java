package andrehsvictor.memorix.token.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDto {

    @NotBlank(message = "Token is required")
    private String token;

}
