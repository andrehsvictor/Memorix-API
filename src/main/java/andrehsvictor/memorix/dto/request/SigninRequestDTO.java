package andrehsvictor.memorix.dto.request;

import lombok.Data;

@Data
public class SigninRequestDTO {
    private String usernameOrEmail;
    private String password;
}
