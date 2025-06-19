package andrehsvictor.memorix.card;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import andrehsvictor.memorix.card.dto.CardStatsDto;

public interface CardRepository extends MongoRepository<Card, UUID> {

    Page<Card> findAllByUserId(UUID userId, Pageable pageable);

    Page<Card> findAllByUserIdAndDueBefore(UUID userId, LocalDateTime due, Pageable pageable);

    Page<Card> findAllByDeckIdAndDueBefore(UUID deckId, LocalDateTime due, Pageable pageable);

    Long countByUserIdAndDueBefore(UUID userId, LocalDateTime due);

    Long countByDeckIdAndDueBefore(UUID deckId, LocalDateTime due);

    @Aggregation(pipeline = {
            "{ '$match': { 'userId': ?0 } }",
            "{ '$group': { '_id': null, " +
                    "'total': { '$sum': 1 }, " +
                    "'due': { '$sum': { '$cond': [{ '$lte': ['$due', new Date()] }, 1, 0] } }, " +
                    "'newCards': { '$sum': { '$cond': [{ '$eq': ['$interval', 0] }, 1, 0] } }, " +
                    "'learning': { '$sum': { '$cond': [{ '$lt': ['$interval', 21] }, 1, 0] } }, " +
                    "'reviewed': { '$sum': { '$cond': [{ '$gt': ['$interval', 20] }, 1, 0] } } } }"
    })
    CardStatsDto findCardStatsByUserId(UUID userId);

    @Aggregation(pipeline = {
            "{ '$match': { 'deckId': ?0 } }",
            "{ '$group': { '_id': null, " +
                    "'total': { '$sum': 1 }, " +
                    "'due': { '$sum': { '$cond': [{ '$lte': ['$due', new Date()] }, 1, 0] } }, " +
                    "'newCards': { '$sum': { '$cond': [{ '$eq': ['$interval', 0] }, 1, 0] } }, " +
                    "'learning': { '$sum': { '$cond': [{ '$lt': ['$interval', 21] }, 1, 0] } }, " +
                    "'reviewed': { '$sum': { '$cond': [{ '$gt': ['$interval', 20] }, 1, 0] } } } }"
    })
    CardStatsDto findCardStatsByDeckId(UUID deckId);

}
