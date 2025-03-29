package andrehsvictor.memorix.review;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("""
            SELECT r
            FROM Review r
            WHERE (
                :query IS NULL
                OR LOWER(r.card.deck.title) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(r.card.deck.author.username) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(r.card.deck.author.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            AND r.deck.id = :deckId
            AND r.user.id = :userId
            """)
    Page<Review> findAllByUserId(
            String query,
            Long userId,
            Pageable pageable);

    Page<Review> findAllByDeckIdAndUserId(
            Long deckId,
            Long userId,
            Pageable pageable);

    Page<Review> findAllByCardIdAndUserId(
            Long cardId,
            Long userId,
            Pageable pageable);

    Optional<Review> findByIdAndUserId(Long id, Long userId);
}
