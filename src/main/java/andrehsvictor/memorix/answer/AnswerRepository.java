package andrehsvictor.memorix.answer;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {

}
