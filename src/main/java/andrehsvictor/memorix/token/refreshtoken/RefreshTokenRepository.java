package andrehsvictor.memorix.token.refreshtoken;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {
    
    Optional<RefreshToken> findByToken(String token);

}
