package andrehsvictor.memorix.token.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccessTokenDto {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;

    @Builder.Default
    private String tokenType = "Bearer";
}
