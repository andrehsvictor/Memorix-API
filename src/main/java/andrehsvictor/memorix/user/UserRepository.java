package andrehsvictor.memorix.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT u FROM User u WHERE u.username = ?1 OR u.email = ?1")
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderId(String providerId);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByEmailVerificationToken(String token);

    Optional<User> findByPasswordResetToken(String token);

    Optional<User> findByEmailChangeToken(String token);

    @Query("""
            SELECT u FROM User u
            WHERE
            (:query IS NULL
            OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(u.displayName) LIKE LOWER(CONCAT('%', :query, '%')))
            AND (:username IS NULL OR u.username = :username)
            AND (:displayName IS NULL OR LOWER(u.displayName) = LOWER(:displayName))
            """)
    Page<User> findAllWithFilters(
            String query,
            String username,
            String displayName,
            Pageable pageable);

}
