package andrehsvictor.memorix.deck;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeckRepository extends JpaRepository<Deck, Long> {

    @Query("SELECT DISTINCT d FROM Deck d LEFT JOIN d.sharedWithUsers du " +
            "WHERE d.visibility = 'PUBLIC' " +
            "OR d.author.id = :userId " +
            "OR (du.user.id = :userId)")
    Page<Deck> findAllAcessible(@Param("userId") Long userId, Pageable pageable);

}