package andrehsvictor.memorix.authentication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostTokenDto {
    private String username;
    private String password;
    private String grantType;
    private String refreshToken;
}
