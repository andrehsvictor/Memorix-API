package andrehsvictor.memorix.review;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findAllByUserId(UUID userId, Pageable pageable);

    Page<Review> findAllByUserIdAndCardId(UUID userId, UUID cardId, Pageable pageable);

    Page<Review> findAllByUserIdAndCardDeckSlug(UUID userId, String slug, Pageable pageable);

}
