package andrehsvictor.memorix.card;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import andrehsvictor.memorix.card.dto.CardStatsDto;

public interface CardRepository extends MongoRepository<Card, UUID> {

    boolean existsByIdAndUserId(UUID id, UUID userId);

    void deleteByDeckId(UUID deckId);

    void deleteByUserId(UUID userId);

    Optional<Card> findByIdAndUserId(UUID id, UUID userId);

    Page<Card> findAllByUserId(UUID userId, Pageable pageable);

    Page<Card> findAllByDeckId(UUID deckId, Pageable pageable);

    Page<Card> findAllByUserIdAndDueBefore(UUID userId, LocalDateTime due, Pageable pageable);

    Page<Card> findAllByDeckIdAndDueBefore(UUID deckId, LocalDateTime due, Pageable pageable);

    Long countByUserIdAndDueBefore(UUID userId, LocalDateTime due);

    Long countByDeckIdAndDueBefore(UUID deckId, LocalDateTime due);

    @Aggregation(pipeline = {
            "{ '$match': { 'userId': ?0 } }",
            "{ '$addFields': { " +
                    "'isNew': { '$eq': ['$interval', 0] }, " +
                    "'isLearning': { '$and': [{ '$gt': ['$interval', 0] }, { '$lt': ['$interval', 21] }] }, " +
                    "'isReviewed': { '$gte': ['$interval', 21] }, " +
                    "'isDue': { '$lte': ['$due', $$NOW] } " +
                    "} }",
            "{ '$group': { " +
                    "'_id': null, " +
                    "'total': { '$sum': 1 }, " +
                    "'due': { '$sum': { '$cond': ['$isDue', 1, 0] } }, " +
                    "'newCards': { '$sum': { '$cond': ['$isNew', 1, 0] } }, " +
                    "'learning': { '$sum': { '$cond': ['$isLearning', 1, 0] } }, " +
                    "'reviewed': { '$sum': { '$cond': ['$isReviewed', 1, 0] } } " +
                    "} }"
    })
    CardStatsDto findCardStatsByUserId(UUID userId);

    @Aggregation(pipeline = {
            "{ '$match': { 'deckId': ?0 } }",
            "{ '$addFields': { " +
                    "'isNew': { '$eq': ['$interval', 0] }, " +
                    "'isLearning': { '$and': [{ '$gt': ['$interval', 0] }, { '$lt': ['$interval', 21] }] }, " +
                    "'isReviewed': { '$gte': ['$interval', 21] }, " +
                    "'isDue': { '$lte': ['$due', $$NOW] } " +
                    "} }",
            "{ '$group': { " +
                    "'_id': null, " +
                    "'total': { '$sum': 1 }, " +
                    "'due': { '$sum': { '$cond': ['$isDue', 1, 0] } }, " +
                    "'newCards': { '$sum': { '$cond': ['$isNew', 1, 0] } }, " +
                    "'learning': { '$sum': { '$cond': ['$isLearning', 1, 0] } }, " +
                    "'reviewed': { '$sum': { '$cond': ['$isReviewed', 1, 0] } } " +
                    "} }"
    })
    CardStatsDto findCardStatsByDeckId(UUID deckId);

    Page<Card> findAllByUserIdAndDueBeforeOrderByDueAsc(
            UUID userId, LocalDateTime due, Pageable pageable);

    Page<Card> findAllByDeckIdAndDueBeforeOrderByDueAsc(
            UUID deckId, LocalDateTime due, Pageable pageable);

    Long countByUserIdAndInterval(UUID userId, Integer interval);

    Long countByDeckIdAndInterval(UUID deckId, Integer interval);
}