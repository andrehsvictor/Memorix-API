package andrehsvictor.memorix.progress;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProgressRepository extends JpaRepository<Progress, UUID> {

    Page<Progress> findAllByUserId(UUID userId, Pageable pageable);

    @Query("SELECT p FROM Progress p WHERE p.user.id = :userId AND p.card.deck.id = :deckId")
    Page<Progress> findAllByUserIdAndDeckId(UUID userId, UUID deckId, Pageable pageable);

    Optional<Progress> findByUserIdAndCardId(UUID userId, UUID cardId);

}
