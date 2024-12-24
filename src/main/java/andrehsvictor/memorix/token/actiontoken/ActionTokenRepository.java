package andrehsvictor.memorix.token.actiontoken;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionTokenRepository extends CrudRepository<ActionToken, String> {

    ActionToken findByToken(String token);

    boolean existsByToken(String token);

}
