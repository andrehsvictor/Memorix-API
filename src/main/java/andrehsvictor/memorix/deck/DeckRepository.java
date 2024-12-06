package andrehsvictor.memorix.deck;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DeckRepository extends JpaRepository<Deck, UUID> {

    Page<Deck> findAllByUsersUserId(UUID userId, Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT d.*
            FROM decks d, decks_users du
            WHERE d.id = :id
            AND ((du.user_id = :userId AND du.deck_id = :id) OR d.visibility = :visibility)
            """)
    Optional<Deck> findByIdAndUsersUserIdOrVisibility(UUID id, UUID userId, String visibility);

    Page<Deck> findAllByVisibility(DeckVisibility visibility, Pageable pageable);

    Optional<Deck> findByIdAndVisibility(UUID id, DeckVisibility visibility);

    Page<Deck> findAllByOwnerUsernameAndVisibility(String username, DeckVisibility visibility, Pageable pageable);

}
