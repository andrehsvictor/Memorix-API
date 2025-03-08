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

    @Query("SELECT u FROM User u WHERE "
            + "LOWER(u.username) ILIKE LOWER(CONCAT('%', ?1, '%')) OR "
            + "LOWER(u.displayName) ILIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<User> findAllByQuery(String query, Pageable pageable);

}
