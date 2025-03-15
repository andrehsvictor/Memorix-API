package andrehsvictor.memorix.deckuser;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DeckUserRepository extends JpaRepository<DeckUser, DeckUserId> {

    boolean existsByUserIdAndDeckIdAndAccessLevel(Long userId, Long deckId, AccessLevel accessLevel);

    boolean existsByUserIdAndDeckId(Long userId, Long deckId);

    Optional<DeckUser> findByUserIdAndDeckId(Long userId, Long deckId);

    @Modifying
    @Transactional
    void deleteByUserIdAndDeckId(Long userId, Long deckId);

    @Modifying
    @Transactional
    void deleteAllByDeckIdAndAccessLevel(Long deckId, AccessLevel accessLevel);

    @Query("""
            SELECT du FROM DeckUser du
            WHERE du.deck.id = :deckId
            AND (:accessLevel IS NULL OR du.accessLevel = :accessLevel)
            AND (
                (:query IS NULL OR LENGTH(TRIM(:query)) = 0)
                OR LOWER(du.user.username) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(du.user.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(du.deck.title) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            """)
    Page<DeckUser> findAllByDeckIdAndAccessLevel(String query, Long deckId, AccessLevel accessLevel, Pageable pageable);

}
