package andrehsvictor.memorix.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiry;
    private Long refreshTokenExpiry;
}
