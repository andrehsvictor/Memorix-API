package andrehsvictor.memorix.card;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("""
            SELECT DISTINCT c
            FROM Card c
            JOIN c.progresses p
            WHERE p.user.id = :userId
            AND (
                :query IS NULL
                OR LOWER(c.front) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(c.back) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(c.author.username) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(c.author.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(c.deck.name) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(c.deck.author.username) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(c.deck.author.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            AND (
                :isAuthor IS NULL
                OR (:isAuthor = TRUE AND c.author.id = :userId)
                OR (:isAuthor = FALSE AND c.author.id <> :userId)
            )
            AND (
                :username IS NULL
                OR c.author.username = :username
            )
                """)
    Page<Card> findAllAccessibleByUserId(
            String query,
            Long userId,
            Boolean isAuthor,
            String username,
            Pageable pageable);

    @Query("""
            SELECT DISTINCT c
            FROM Card c
            LEFT JOIN c.progresses p WITH p.user.id = :userId
            LEFT JOIN c.deck.usersWithAccess uwa WITH uwa.id = :userId
            WHERE c.deck.id = :deckId
            AND (
                :query IS NULL
                OR LOWER(c.front) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(c.back) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(c.deck.name) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(c.author.username) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(c.author.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            AND (
                c.deck.visibility = 'PUBLIC'
                OR c.author.id = :userId
                OR p.id IS NOT NULL
                OR uwa.id IS NOT NULL
            )
            AND (
                :isAuthor IS NULL
                OR (:isAuthor = TRUE AND c.author.id = :userId)
                OR (:isAuthor = FALSE AND c.author.id <> :userId)
            )
            AND (
                :username IS NULL
                OR c.author.username = :username
            )
            """)
    Page<Card> findAllAccessibleOrVisibleByDeckIdAndUserId(
            String query,
            Long deckId,
            Long userId,
            Boolean isAuthor,
            String username,
            Pageable pageable);

    @Query("""
            SELECT c
            FROM Card c
            LEFT JOIN c.progresses p
            ON p.user.id = :userId
            WHERE c.id = :id
            AND (
                c.deck.visibility = 'PUBLIC'
                OR c.author.id = :userId
                OR p.id IS NOT NULL
            )
            """)
    Optional<Card> findVisibleByIdAndUserId(Long id, Long userId);

}
