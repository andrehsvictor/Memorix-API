package andrehsvictor.memorix.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CredentialsDto {

    @NotBlank(message = "Username or email must not be blank")
    private String username; // or email

    @NotBlank(message = "Password must not be blank")
    private String password;
    
}
