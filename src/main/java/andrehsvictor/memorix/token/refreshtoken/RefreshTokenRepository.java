package andrehsvictor.memorix.token.refreshtoken;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {

    boolean existsByToken(String token);

}
