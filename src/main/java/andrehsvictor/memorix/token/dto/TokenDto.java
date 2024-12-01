package andrehsvictor.memorix.token.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDto {

    @NotBlank(message = "Token must be provided.")
    private String token;

}
