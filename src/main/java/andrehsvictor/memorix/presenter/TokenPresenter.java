package andrehsvictor.memorix.presenter;

import java.time.ZoneOffset;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.dto.ResponseBody;
import andrehsvictor.memorix.dto.response.TokenResponseDTO;
import andrehsvictor.memorix.entity.RefreshToken;

@Service
public class TokenPresenter {

    public ResponseBody<TokenResponseDTO> present(Jwt accessToken, RefreshToken refreshToken) {
        return ResponseBody.<TokenResponseDTO>builder()
                .data(TokenResponseDTO.builder()
                        .accessToken(accessToken.getTokenValue())
                        .refreshToken(refreshToken.getToken())
                        .accessTokenExpiry(accessToken.getExpiresAt().toEpochMilli())
                        .refreshTokenExpiry(refreshToken.getExpiresAt().toInstant(ZoneOffset.UTC).toEpochMilli())
                        .build())
                .build();
    }
}
