package andrehsvictor.memorix.token.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenDto {

    @NotBlank(message = "The refresh token must be provided.")
    private String refreshToken;

}
