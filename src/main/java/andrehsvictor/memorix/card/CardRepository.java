package andrehsvictor.memorix.card;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import andrehsvictor.memorix.card.dto.CardFilterDto;

public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("""
            SELECT DISTINCT c
            FROM Card c
            JOIN c.progresses p
            ON p.user.id = :userId
            WHERE (
                (:#{#cardFilterDto.q} IS NULL OR
                (:#{#cardFilterDto.q} LIKE )
            )
            AND (
                (:isAuthor IS NULL)
                OR (:isAuthor = TRUE AND c.author.id = :userId)
                OR (:isAuthor = FALSE AND c.author.id <> :userId)
            )
            AND (
                (:username IS NULL OR LENGTH(TRIM(:username)) = 0)
                OR c.author.username = :username
            )
                """)
    Page<Card> findAllAcessibleByUserId(
            CardFilterDto cardFilterDto,
            Long userId,
            Pageable pageable);

    @Query("""
            SELECT DISTINCT c
            FROM Card c
            LEFT JOIN c.progresses p
            ON p.user.id = :userId
            LEFT JOIN c.deck.usersWithAccess du
            ON du.user.id = :userId
            WHERE c.deck.id = :deckId
            AND (
                (:query IS NULL OR LENGTH(TRIM(:query)) = 0)
                OR LOWER(c.front) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(c.deck.name) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(c.author.username) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(c.author.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            AND (
                c.deck.visibility = 'PUBLIC'
                OR c.author.id = :userId
                OR p.id IS NOT NULL
            )
            AND (
                (:isAuthor IS NULL)
                OR (:isAuthor = TRUE AND c.author.id = :userId)
                OR (:isAuthor = FALSE AND c.author.id <> :userId)
            )
            AND (
                (:username IS NULL OR LENGTH(TRIM(:username)) = 0)
                OR c.author.username = :username
            )
            """)
    Page<Card> findAllAcessibleOrVisibleByDeckIdAndUserId(
            String query,
            Long deckId,
            Long userId,
            boolean isAuthor,
            String username,
            Pageable pageable);

    @Query("""
            SELECT DISTINCT c
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
