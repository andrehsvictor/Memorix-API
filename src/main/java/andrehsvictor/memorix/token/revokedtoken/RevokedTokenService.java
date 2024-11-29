package andrehsvictor.memorix.token.revokedtoken;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RevokedTokenService {

    private final RevokedTokenRepository revokedTokenRepository;

    public void revoke(String token) {
        RevokedToken revokedToken = new RevokedToken(token);
        revokedTokenRepository.save(revokedToken);
    }

    public boolean isRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }
}
