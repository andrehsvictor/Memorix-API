package andrehsvictor.memorix.review;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.card.Card;
import andrehsvictor.memorix.review.dto.PostReviewDto;
import andrehsvictor.memorix.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Review create(PostReviewDto postReviewDto, User user, Card card) {
        Review review = new Review();
        review.setRating(postReviewDto.getRating());
        review.setTimeToAnswer(postReviewDto.getTimeToAnswer());
        review.setUser(user);
        review.setCard(card);
        return reviewRepository.save(review);
    }
}
