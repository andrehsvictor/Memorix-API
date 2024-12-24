package andrehsvictor.memorix.token.actiontoken;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionTokenRepository extends CrudRepository<ActionToken, UUID> {

    ActionToken findByToken(String token);

    boolean existsByToken(String token);

    void deleteByToken(String token);

}
