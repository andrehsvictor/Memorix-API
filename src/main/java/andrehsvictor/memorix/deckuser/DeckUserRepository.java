package andrehsvictor.memorix.deckuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface DeckUserRepository extends JpaRepository<DeckUser, DeckUserId> {

    boolean existsByUserIdAndDeckIdAndAccessLevel(Long userId, Long deckId, AccessLevel accessLevel);

    boolean existsByUserIdAndDeckId(Long userId, Long deckId);

    @Modifying
    @Transactional
    void deleteByUserIdAndDeckId(Long userId, Long deckId);

    @Modifying
    @Transactional
    void deleteByUserIdAndDeckIdAndAccessLevel(Long userId, Long deckId, AccessLevel accessLevel);

}
