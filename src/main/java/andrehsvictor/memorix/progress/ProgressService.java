package andrehsvictor.memorix.progress;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.review.dto.CreateReviewDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;

    public Progress progress(Long userId, Long cardId, CreateReviewDto createReviewDto) {
        Progress progress = progressRepository.findByUserIdAndCardId(userId, cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress not found"));
        return updateProgress(progress, createReviewDto);
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

}
