package andrehsvictor.memorix.deck;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import andrehsvictor.memorix.deckuser.AccessLevel;

public interface DeckRepository extends JpaRepository<Deck, Long> {

    @Query("""
            SELECT d
            FROM Deck d
            WHERE (
                :query IS NULL
                OR LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(d.author.username) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(d.author.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            AND d.visibility = :visibility
            """)
    Page<Deck> findAllByVisibility(String query, DeckVisibility visibility, Pageable pageable);

    @Query("""
            SELECT DISTINCT d 
            FROM Deck d
            LEFT JOIN d.usersWithAccess du 
            ON du.user.id = :userId
            WHERE (
                d.author.id = :userId
                OR du IS NOT NULL
                OR d.visibility = 'PUBLIC'
            )
            AND (
                :query IS NULL
                OR LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(d.author.username) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(d.author.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            AND (:visibility IS NULL OR d.visibility = :visibility)
            AND (:accessLevel IS NULL OR du.accessLevel = :accessLevel)
            """)
    Page<Deck> findAllAccessibleByUserId(String query, DeckVisibility visibility, AccessLevel accessLevel, Long userId,
            Pageable pageable);

    @Query("""
            SELECT d FROM Deck d
            WHERE d.author.id = :authorId
            AND d.visibility = :visibility
            AND (
                :query IS NULL
                OR LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            """)
    Page<Deck> findAllByAuthorIdAndVisibility(String query, Long authorId, DeckVisibility visibility,
            Pageable pageable);

    @Query("""
            SELECT d FROM Deck d
            LEFT JOIN d.usersWithAccess du
            ON d.id = du.deck.id
            WHERE d.id = :id
            AND (
                d.author.id = :userId
                OR du.user.id = :userId
                OR d.visibility = 'PUBLIC'
            )
            """)
    Optional<Deck> findVisibleByIdAndUserId(Long id, Long userId);

    @NativeQuery("""
            SELECT COUNT(*) > 0 FROM decks_users_likes
            WHERE user_id = :userId
            AND deck_id = :deckId
            """)
    boolean isLikedByUser(Long deckId, Long userId);

    @Modifying
    @Transactional
    @NativeQuery("INSERT INTO decks_users_likes (user_id, deck_id) VALUES (:userId, :deckId)")
    void like(Long deckId, Long userId);

    @Modifying
    @Transactional
    @NativeQuery("DELETE FROM decks_users_likes WHERE user_id = :userId AND deck_id = :deckId")
    void unlike(Long deckId, Long userId);

}