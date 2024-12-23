package andrehsvictor.memorix.progress;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.card.Card;
import andrehsvictor.memorix.card.CardService;
import andrehsvictor.memorix.review.dto.PostReviewDto;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final UserService userService;
    private final CardService cardService;

    public Progress getByUserIdAndCardId(UUID userId, UUID cardId) {
        Optional<Progress> optProgress = progressRepository.findByUserIdAndCardId(userId, cardId);
        if (optProgress.isEmpty()) {
            Progress progress = new Progress();
            User user = userService.getById(userId);
            Card card = cardService.getByIdAndUserId(cardId, userId);
            progress.setUser(user);
            progress.setCard(card);
            return progressRepository.save(progress);
        }
        return optProgress.get();
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
        progressRepository.save(progress);
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
