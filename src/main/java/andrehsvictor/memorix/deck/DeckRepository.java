package andrehsvictor.memorix.deck;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface DeckRepository extends JpaRepository<Deck, UUID> {

    Optional<Deck> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);

    boolean existsByNameAndUserId(String name, UUID userId);

    void deleteByIdAndUserId(UUID id, UUID userId);

    Page<Deck> findAllByUserId(UUID userId, Pageable pageable);

    @Modifying
    @Transactional
    Integer deleteAllByIdInAndUserId(Set<UUID> ids, UUID userId);

}
