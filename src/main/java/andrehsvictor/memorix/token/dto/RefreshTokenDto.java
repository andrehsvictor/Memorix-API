package andrehsvictor.memorix.token.dto;

import andrehsvictor.memorix.token.validation.NotRevoked;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenDto {

    @NotRevoked
    @NotBlank(message = "The refresh token must be provided.")
    private String refreshToken;

}
