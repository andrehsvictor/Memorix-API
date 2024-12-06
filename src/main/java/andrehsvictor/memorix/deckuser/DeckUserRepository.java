package andrehsvictor.memorix.deckuser;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

public interface DeckUserRepository extends JpaRepository<DeckUser, DeckUserId> {

    @Modifying
    @Transactional
    @Query("DELETE FROM DeckUser du WHERE du.id.deckId = :deckId AND du.deck.owner.id != du.id.userId")
    void deleteAllByDeckIdExceptOwner(UUID deckId);

    @Modifying
    @Transactional
    void deleteByDeckIdAndUserId(UUID deckId, UUID userId);

    boolean existsByDeckIdAndUserId(UUID deckId, UUID userId);

}
