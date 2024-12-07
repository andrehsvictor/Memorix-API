package andrehsvictor.memorix.deck;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckRepository extends JpaRepository<Deck, UUID> {

    Optional<Deck> findBySlugAndUserId(String slug, UUID userId);

    void deleteBySlug(String slug);

    boolean existsBySlugAndUserId(String slug, UUID userId);

    Page<Deck> findAllByUserId(UUID userId, Pageable pageable);

}
