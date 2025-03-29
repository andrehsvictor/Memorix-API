package andrehsvictor.memorix.progress;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.card.Card;
import andrehsvictor.memorix.card.CardService;
import andrehsvictor.memorix.deckuser.DeckUserService;
import andrehsvictor.memorix.exception.ForbiddenOperationException;
import andrehsvictor.memorix.exception.ResourceConflictException;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.review.dto.CreateReviewDto;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final CardService cardService;
    private final UserService userService;
    private final DeckUserService deckUserService;

    public Progress progress(Long userId, Long cardId, CreateReviewDto createReviewDto) {
        Progress progress = progressRepository.findByUserIdAndCardId(userId, cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress not found"));
        return updateProgress(progress, createReviewDto);
    }

    public boolean exists(Long userId, Long cardId) {
        return progressRepository.existsByUserIdAndCardId(userId, cardId);
    }

    private Progress updateProgress(Progress progress, CreateReviewDto createReviewDto) {
        Integer totalAnswers = progress.getTotalAnswers() + 1;
        Float newMeanRating = ((progress.getMeanRating() * progress.getTotalAnswers())
                + createReviewDto.getRating()) / totalAnswers;
        Float newMeanTimeToAnswer = ((progress.getMeanTimeToAnswer() * progress.getTotalAnswers())
                + createReviewDto.getTimeToAnswer()) / totalAnswers;

        progress.setTotalAnswers(totalAnswers);
        progress.setMeanRating(newMeanRating);
        progress.setMeanTimeToAnswer(newMeanTimeToAnswer);
        progress.setLastReviewedAt(LocalDateTime.now());

        Integer rating = createReviewDto.getRating();
        Float easinessFactor = progress.getEasinessFactor();

        Float newEasinessFactor = easinessFactor + (0.1f - (5 - rating) * (0.08f + (5 - rating) * 0.02f));

        if (newEasinessFactor < 1.3f) {
            newEasinessFactor = 1.3f;
        }

        Integer repetitions = progress.getRepetitions();
        Integer consecutiveCorrectAnswers = progress.getConsecutiveCorrectAnswers();
        Integer interval = progress.getInterval();

        if (rating < 3) {
            consecutiveCorrectAnswers = 0;
            interval = Math.max(1, interval / 2);
        } else {
            consecutiveCorrectAnswers += 1;

            if (consecutiveCorrectAnswers == 1) {
                interval = 1;
            } else if (consecutiveCorrectAnswers == 2) {
                interval = 6;
            } else {
                interval = Math.round(interval * newEasinessFactor);
            }
        }

        progress.setEasinessFactor(newEasinessFactor);
        progress.setConsecutiveCorrectAnswers(consecutiveCorrectAnswers);
        progress.setRepetitions(repetitions + 1);
        progress.setInterval(interval);

        progress.setNextReviewAt(LocalDateTime.now().plusDays(interval).toLocalDate().atStartOfDay());

        return progressRepository.save(progress);
    }

    public Progress create(Long cardId) {
        User user = userService.findMyself();
        if (exists(user.getId(), cardId)) {
            throw new ResourceConflictException("Progress already exists for this card");
        }
        Progress progress = new Progress();
        progress.setUser(user);
        Card card = cardService.findById(cardId);
        if (!deckUserService.hasAccess(user.getId(), card.getDeck().getId())) {
            throw new ForbiddenOperationException("You don't have permission to create a progress for this card");
        }
        progress.setCard(card);
        return progressRepository.save(progress);
    }

}
