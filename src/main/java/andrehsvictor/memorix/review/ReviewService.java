package andrehsvictor.memorix.review;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.card.Card;
import andrehsvictor.memorix.card.CardService;
import andrehsvictor.memorix.exception.ForbiddenActionException;
import andrehsvictor.memorix.progress.Progress;
import andrehsvictor.memorix.progress.ProgressService;
import andrehsvictor.memorix.review.dto.PostReviewDto;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProgressService progressService;
    private final ReviewMapper reviewMapper;
    private final UserService userService;
    private final CardService cardService;

    public Review create(PostReviewDto postReviewDto, UUID userId, UUID cardId) {
        Progress progress = progressService.getByUserIdAndCardId(userId, cardId);
        if (progress.getNextRepetition().isAfter(LocalDateTime.now())) {
            throw new ForbiddenActionException("You can't review this card yet");
        }
        User user = userService.getById(userId);
        Card card = cardService.getByIdAndDeckUserId(cardId, userId);
        Review review = reviewMapper.postReviewDtoToReview(postReviewDto);
        review.setUser(user);
        review.setCard(card);
        review = reviewRepository.save(review);
        progressService.progress(progress, postReviewDto);
        return review;
    }

}
