package andrehsvictor.memorix.progress;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgressRepository extends JpaRepository<Progress, UUID> {

}
