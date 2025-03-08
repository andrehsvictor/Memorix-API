package andrehsvictor.memorix.token.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessTokenDto {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;

    @Builder.Default
    private String tokenType = "Bearer";
}
