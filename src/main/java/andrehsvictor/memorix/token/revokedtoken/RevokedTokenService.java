package andrehsvictor.memorix.token.revokedtoken;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.token.jwt.JwtService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RevokedTokenService {

    private final RevokedTokenRepository revokedTokenRepository;
    private final JwtService jwtService;

    public void revoke(String token) {
        Long expiresIn = jwtService.getRemainingLifetime(token, TimeUnit.SECONDS);
        RevokedToken revokedToken = RevokedToken.of(token, expiresIn);
        revokedTokenRepository.save(revokedToken);
    }

    public boolean isRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }

}
