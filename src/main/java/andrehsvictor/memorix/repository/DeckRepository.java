package andrehsvictor.memorix.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import andrehsvictor.memorix.entity.Deck;
import andrehsvictor.memorix.entity.User;

public interface DeckRepository extends JpaRepository<Deck, Long> {

    @Query("SELECT d FROM Deck d WHERE d.user = :user")
    Page<Deck> findAll(User user, Pageable pageable);

    @Query("SELECT d FROM Deck d WHERE d.slug = :slug AND d.user = :user")
    Optional<Deck> findBySlug(String slug, User user);

    @Query("SELECT d FROM Deck d WHERE d.id = :id AND d.user = :user")
    Optional<Deck> findById(Long id, User user);
}
