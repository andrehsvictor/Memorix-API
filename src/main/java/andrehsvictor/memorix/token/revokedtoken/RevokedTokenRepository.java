package andrehsvictor.memorix.token.revokedtoken;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevokedTokenRepository extends CrudRepository<RevokedToken, UUID> {

    boolean existsByToken(String token);

}
