package andrehsvictor.memorix.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IdTokenDto {

    @NotBlank(message = "ID token must not be blank")
    private String idToken;

}
