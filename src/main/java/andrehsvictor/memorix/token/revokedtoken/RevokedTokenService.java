package andrehsvictor.memorix.token.revokedtoken;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.exception.UnauthorizedException;
import andrehsvictor.memorix.token.jwt.JwtService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RevokedTokenService {

    private final RevokedTokenRepository revokedTokenRepository;
    private final JwtService jwtService;

    public RevokedToken save(RevokedToken revokedToken) {
        return revokedTokenRepository.save(revokedToken);
    }

    public void revoke(String token) {
        Jwt jwt = jwtService.decode(token);
        UUID id = UUID.fromString(jwt.getId());
        Long ttl = jwtService.getRemainingLifespan(token, TimeUnit.SECONDS);
        RevokedToken revokedToken = RevokedToken.of(id, ttl);
        revokedTokenRepository.save(revokedToken);
    }

    public boolean existsById(UUID id) {
        return revokedTokenRepository.existsById(id);
    }

    public void assertNotExistsById(UUID id) {
        if (existsById(id)) {
            throw new UnauthorizedException("The token has been revoked.");
        }
    }
}
