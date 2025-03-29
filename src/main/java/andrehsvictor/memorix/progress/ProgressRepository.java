package andrehsvictor.memorix.progress;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgressRepository extends JpaRepository<Progress, Long> {

    Optional<Progress> findByUserIdAndCardId(Long userId, Long cardId);

    boolean existsByUserIdAndCardId(Long userId, Long cardId);

}
