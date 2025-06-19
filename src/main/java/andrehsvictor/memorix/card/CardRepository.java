package andrehsvictor.memorix.card;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CardRepository extends MongoRepository<Card, UUID> {

    Page<Card> findAllByUserId(UUID userId, Pageable pageable);

    Page<Card> findAllByUserIdAndDueBefore(UUID userId, LocalDateTime due, Pageable pageable);

    Page<Card> findAllByDeckIdAndDueBefore(UUID deckId, LocalDateTime due, Pageable pageable);

    Long countByUserIdAndDueBefore(UUID userId, LocalDateTime due);

    Long countByDeckIdAndDueBefore(UUID deckId, LocalDateTime due);

}
