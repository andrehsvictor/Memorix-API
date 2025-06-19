package andrehsvictor.memorix.review;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewRepository extends MongoRepository<Review, UUID> {

    Page<Review> findAllByUserId(UUID userId, Pageable pageable);

    Page<Review> findAllByCardIdAndUserId(UUID cardId, UUID userId, Pageable pageable);

    Optional<Review> findByIdAndUserId(UUID id, UUID userId);

    void deleteByUserId(UUID userId);

    void deleteByCardId(UUID cardId);

}
