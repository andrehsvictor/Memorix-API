package andrehsvictor.memorix.deck;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckRepository extends JpaRepository<Deck, UUID> {

    Optional<Deck> findBySlugAndUserId(String slug, UUID userId);

    boolean existsBySlugAndUserId(String slug, UUID userId);
    
}
