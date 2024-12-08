package andrehsvictor.memorix.deck;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DeckRepository extends JpaRepository<Deck, UUID> {

    Optional<Deck> findBySlugAndUserId(String slug, UUID userId);

    boolean existsBySlugAndUserId(String slug, UUID userId);

    void deleteBySlugAndUserId(String slug, UUID userId);

    Page<Deck> findAllByUserId(UUID userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM Deck d WHERE d.slug IN :slugs AND d.user.id = :userId")
    void deleteAllWithSlugsAndUserId(Set<String> slugs, UUID userId);

}
