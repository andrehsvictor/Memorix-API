package andrehsvictor.memorix.deck;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeckRepository extends JpaRepository<Deck, Long> {

    List<Deck> findByAuthorId(Long authorId);

    Page<Deck> findByAuthorId(Long authorId, Pageable pageable);

    @Query("SELECT d FROM Deck d WHERE d.visibility = 'PUBLIC' ORDER BY d.likesCount DESC")
    Page<Deck> findPopularDecks(Pageable pageable);

    @Query("SELECT d FROM Deck d WHERE d.id = :id AND (d.visibility = 'PUBLIC' OR d.author.id = :userId)")
    Optional<Deck> findByIdAndPublicOrOwnedBy(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT d FROM Deck d JOIN d.sharedWithUsers du WHERE du.user.id = :userId AND du.accessLevel IN ('EDITOR', 'VIEWER')")
    Page<Deck> findSharedWithUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT d FROM Deck d WHERE d.visibility = 'PUBLIC' AND (LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Deck> searchPublicDecks(@Param("query") String query, Pageable pageable);

    @Query("SELECT d FROM Deck d WHERE (d.visibility = 'PUBLIC' OR d.author.id = :userId OR EXISTS (SELECT du FROM d.sharedWithUsers du WHERE du.user.id = :userId AND du.accessLevel IN ('EDITOR', 'VIEWER'))) AND (LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Deck> searchAccessibleDecks(@Param("query") String query, @Param("userId") Long userId, Pageable pageable);
}