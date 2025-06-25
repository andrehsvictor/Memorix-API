package andrehsvictor.memorix.review;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ReviewRepository extends MongoRepository<Review, UUID> {

    @Query("""
            {
                'userId': ?0,
                $and: [
                    { $or: [ { $expr: { $eq: [?1, null] } }, { 'rating': { $gte: ?1 } } ] },
                    { $or: [ { $expr: { $eq: [?2, null] } }, { 'rating': { $lte: ?2 } } ] },
                    { $or: [ { $expr: { $eq: [?3, null] } }, { 'responseTime': { $gte: ?3 } } ] },
                    { $or: [ { $expr: { $eq: [?4, null] } }, { 'responseTime': { $lte: ?4 } } ] }
                ]
            }
            """)
    Page<Review> findAllByUserIdWithFilters(
            UUID userId,
            Integer minRating,
            Integer maxRating,
            Integer minResponseTime,
            Integer maxResponseTime,
            Pageable pageable);

    Page<Review> findAllByCardIdAndUserId(UUID cardId, UUID userId, Pageable pageable);

    Optional<Review> findByIdAndUserId(UUID id, UUID userId);

    void deleteByUserId(UUID userId);

    void deleteByCardId(UUID cardId);

}
