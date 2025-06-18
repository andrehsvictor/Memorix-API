package andrehsvictor.memorix.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeEmailDto {

    @NotBlank(message = "Token cannot be blank")
    private String token;

}
