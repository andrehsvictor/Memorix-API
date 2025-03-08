package andrehsvictor.memorix.deckuser;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckUserRepository extends JpaRepository<DeckUser, DeckUserId> {

    boolean existsByUserIdAndDeckIdAndAccessLevel(Long userId, Long deckId, AccessLevel accessLevel);

}
