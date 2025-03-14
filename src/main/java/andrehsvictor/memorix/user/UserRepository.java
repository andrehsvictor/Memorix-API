package andrehsvictor.memorix.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = ?1 OR u.email = ?1")
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            SELECT u FROM User u
            WHERE (
                (:query IS NULL OR LENGTH(TRIM(:query)) = 0)
                OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(u.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
            )
                """)
    Page<User> findAll(String query, Pageable pageable);

}
