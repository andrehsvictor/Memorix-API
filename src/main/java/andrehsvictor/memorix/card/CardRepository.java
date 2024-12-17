package andrehsvictor.memorix.card;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, UUID> {

    Optional<Card> findByIdAndDeckUserId(UUID id, UUID userId);

    Page<Card> findAllByDeckUserId(UUID userId, Pageable pageable);

    Page<Card> findAllByDeckId(UUID deckId, Pageable pageable);

    void deleteByIdAndDeckUserId(UUID id, UUID userId);

}
