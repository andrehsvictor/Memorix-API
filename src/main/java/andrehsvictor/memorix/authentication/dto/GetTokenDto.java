package andrehsvictor.memorix.authentication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetTokenDto {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private Long refreshTokenExpiresIn;
}
