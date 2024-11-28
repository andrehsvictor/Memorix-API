package andrehsvictor.memorix.token.revokedtoken;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, UUID> {

}
