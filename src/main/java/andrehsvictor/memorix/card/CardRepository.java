package andrehsvictor.memorix.card;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, UUID> {

    boolean existsByIdAndDeckUserId(UUID cardId, UUID userId);

    void deleteByIdAndDeckUserId(UUID cardId, UUID userId);

    Optional<Card> findByIdAndDeckUserId(UUID cardId, UUID userId);

    Page<Card> findAllByDeckUserId(UUID userId, Pageable pageable);

    Page<Card> findAllByDeckUserIdAndDeckId(UUID userId, UUID deckId, Pageable pageable);

    Integer countByDeckUserIdAndProgressNextRepetitionBefore(UUID userId, LocalDateTime nextRepetition);

    Page<Card> findAllByDeckUserIdAndProgressNextRepetitionBefore(UUID userId, LocalDateTime nextRepetition,
            Pageable pageable);

}
