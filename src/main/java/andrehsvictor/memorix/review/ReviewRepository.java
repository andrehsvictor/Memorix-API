package andrehsvictor.memorix.review;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    @Query("SELECT r FROM Review r WHERE r.progress.user.id = :userId")
    Page<Review> findAllByUserId(UUID userId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.progress.user.id = :userId AND r.progress.card.id = :cardId")
    Page<Review> findAllByUserIdAndCardId(UUID userId, UUID cardId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.progress.user.id = :userId AND r.progress.card.deck.slug = :slug")
    Page<Review> findAllByUserIdAndDeckSlug(UUID userId, String slug, Pageable pageable);

}
