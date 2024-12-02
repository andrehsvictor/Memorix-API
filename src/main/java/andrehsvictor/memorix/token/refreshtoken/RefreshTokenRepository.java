package andrehsvictor.memorix.token.refreshtoken;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {

}
