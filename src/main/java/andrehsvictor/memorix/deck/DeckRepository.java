package andrehsvictor.memorix.deck;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

public interface DeckRepository extends JpaRepository<Deck, UUID> {

    @Modifying
    @Transactional
    void deleteByUserId(UUID userId);

    boolean existsByNameAndUserId(String name, UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);

    Optional<Deck> findByIdAndUserId(UUID id, UUID userId);

    Optional<Deck> findByNameAndUserId(String name, UUID userId);

    @Query("""
            SELECT d FROM Deck d
            WHERE d.user.id = :userId
            AND (
                LOWER(:query) IS NULL OR
                LOWER(d.name) LIKE CONCAT('%', LOWER(:query), '%') OR
                LOWER(d.description) LIKE CONCAT('%', LOWER(:query), '%')
            )
            AND (LOWER(:name) IS NULL OR LOWER(d.name) = LOWER(:name))
            AND (LOWER(:description) IS NULL OR LOWER(d.description) LIKE CONCAT('%', LOWER(:description), '%'))
            AND (:includeWithCoverImage IS NULL OR
                (:includeWithCoverImage = TRUE AND d.coverImageUrl IS NOT NULL) OR
                (:includeWithCoverImage = FALSE AND d.coverImageUrl IS NULL))
            AND (:includeEmptyDecks IS NULL OR
                (:includeEmptyDecks = TRUE AND d.cardCount = 0) OR
                (:includeEmptyDecks = FALSE AND d.cardCount > 0))
            """)
    Page<Deck> findAllByUserIdWithFilters(
            UUID userId,
            String query,
            String name,
            String description,
            Boolean includeWithCoverImage,
            Boolean includeEmptyDecks,
            Pageable pageable);

}
