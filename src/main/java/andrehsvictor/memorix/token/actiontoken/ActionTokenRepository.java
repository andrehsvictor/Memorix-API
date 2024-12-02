package andrehsvictor.memorix.token.actiontoken;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface ActionTokenRepository extends CrudRepository<ActionToken, UUID> {

    ActionToken findByToken(String token);

    ActionToken findByUserId(String userId);

    void deleteByToken(String token);

    void deleteByUserId(String userId);

}
