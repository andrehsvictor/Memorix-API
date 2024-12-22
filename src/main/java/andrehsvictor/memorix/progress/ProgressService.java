package andrehsvictor.memorix.progress;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.card.Card;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.review.dto.PostReviewDto;
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

    public Progress getByUserIdAndCardId(UUID userId, UUID cardId) {
        return progressRepository.findByUserIdAndCardId(userId, cardId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Progress not found with user ID '" + userId + "' and card ID '" + cardId + "'"));
    }

    public void progress(Progress progress, PostReviewDto postReviewDto) {
        Integer rating = postReviewDto.getRating();
        Integer timeToAnswer = postReviewDto.getTimeToAnswer();

        progress.setReviewsCount(progress.getReviewsCount() + 1);

        Float newEaseFactor = calculateEaseFactor(progress.getEaseFactor(), rating);
        if (newEaseFactor < 1.3f) {
            newEaseFactor = 1.3f;
        }
        progress.setEaseFactor(newEaseFactor);

        if (rating < 3) {
            progress.setRepetitions(0);
            progress.setInterval(1);
            progress.setMisses(progress.getMisses() + 1);
            progress.setStatus(ProgressStatus.FORGOTTEN);
        } else {
            progress.setRepetitions(progress.getRepetitions() + 1);
            if (progress.getRepetitions() == 1) {
                progress.setInterval(1);
                progress.setStatus(ProgressStatus.LEARNING);
            } else if (progress.getRepetitions() == 2) {
                progress.setInterval(6);
            } else {
                progress.setInterval(
                        calculateInterval(progress.getInterval(), progress.getRepetitions(), progress.getEaseFactor()));
                progress.setStatus(ProgressStatus.REVIEWING);
            }
            progress.setHits(progress.getHits() + 1);
        }

        Float newAverageTimeToAnswer = (progress.getAverageTimeToAnswer()
                * (progress.getHits() + progress.getMisses() - 1) + timeToAnswer)
                / (progress.getHits() + progress.getMisses());
        progress.setAverageTimeToAnswer(newAverageTimeToAnswer);

        progress.setLastStudied(LocalDateTime.now());
        progress.setNextRepetition(calculateNextRepetition(progress.getInterval()));
    }

    private LocalDateTime calculateNextRepetition(Integer interval) {
        return LocalDate.now().plusDays(interval).atStartOfDay();
    }

    private Float calculateEaseFactor(Float easeFactor, Integer timeToAnswer) {
        return easeFactor + (0.1f - (5 - timeToAnswer) * (0.08f + (5 - timeToAnswer) * 0.02f));
    }

    private Integer calculateInterval(Integer interval, Integer repetitions, Float easeFactor) {
        if (repetitions == 0) {
            return 1;
        } else if (repetitions == 1) {
            return 6;
        } else {
            return Math.round(interval * easeFactor);
        }
    }

}
