package andrehsvictor.memorix.deck;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DeckRepository extends JpaRepository<Deck, Long> {

    @Query("SELECT d FROM Deck d LEFT JOIN d.usersWithAccess du " +
            "WHERE d.id = ?1 " +
            "AND (d.author.id = ?2 " +
            "OR du.user.id = ?2) " +
            "OR d.visibility = 'PUBLIC'")
    Optional<Deck> findAccessibleByIdAndUserId(Long id, Long userId);

    @Query("SELECT DISTINCT d FROM Deck d LEFT JOIN d.usersWithAccess du " +
            "WHERE d.author.id = ?1 " +
            "OR du.user.id = ?1")
    Page<Deck> findAllAccessibleByUserId(Long userId, Pageable pageable);

    Page<Deck> findAllByAuthorIdAndVisibility(Long authorId, DeckVisibility visibility, Pageable pageable);

    Page<Deck> findAllByAuthorId(Long authorId, Pageable pageable);

}