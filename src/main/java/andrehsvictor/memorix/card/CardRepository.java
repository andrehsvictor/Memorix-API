package andrehsvictor.memorix.card;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, UUID> {

    boolean existsByIdAndDeckUserId(UUID cardId, UUID userId);

    void deleteByIdAndDeckUserId(UUID cardId, UUID userId);

}
