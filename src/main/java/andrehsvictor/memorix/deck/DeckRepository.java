package andrehsvictor.memorix.deck;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckRepository extends JpaRepository<Deck, UUID> {

    Page<Deck> findAllByUsersUserId(UUID userId, Pageable pageable);

    Optional<Deck> findByIdAndUsersUserIdOrVisibility(UUID id, UUID userId, DeckVisibility visibility);

    Page<Deck> findAllByVisibility(DeckVisibility visibility, Pageable pageable);

    Optional<Deck> findByIdAndVisibility(UUID id, DeckVisibility visibility);

    Page<Deck> findAllByOwnerUsernameAndVisibility(String username, DeckVisibility visibility, Pageable pageable);

}
