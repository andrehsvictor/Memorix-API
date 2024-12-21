package andrehsvictor.memorix.progress;

import java.util.UUID;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.card.Card;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;

    public Progress create(User user, Card card) {
        Progress progress = new Progress();
        progress.setUser(user);
        progress.setCard(card);
        return progressRepository.save(progress);
    }

    public Progress review(Progress progress, Integer rating, Integer timeToAnswer) {
        progress.review(rating, timeToAnswer);
        return progressRepository.save(progress);
    }

    public Progress getByUserIdAndCardId(UUID userId, UUID cardId) {
        return progressRepository.findByUserIdAndCardId(userId, cardId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Progress not found with user ID '" + userId + "' and card ID '" + cardId + "'"));
    }

}
