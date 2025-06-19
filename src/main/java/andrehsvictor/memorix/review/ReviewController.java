package andrehsvictor.memorix.review;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.review.dto.CreateReviewDto;
import andrehsvictor.memorix.review.dto.ReviewDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/api/v1/reviews")
    public Page<ReviewDto> getAllReviews(
            Integer minRating,
            Integer maxRating,
            Integer minResponseTime,
            Integer maxResponseTime,
            Pageable pageable) {
        Page<Review> reviews = reviewService.getAll(minRating, maxRating, minResponseTime, maxResponseTime, pageable);
        return reviews.map(reviewService::toDto);
    }

    @GetMapping("/api/v1/cards/{cardId}/reviews")
    public Page<ReviewDto> getReviewsByCardId(@PathVariable UUID cardId, Pageable pageable) {
        Page<Review> reviews = reviewService.getAllByCardId(cardId, pageable);
        return reviews.map(reviewService::toDto);
    }

    @PostMapping("/api/v1/cards/{cardId}/reviews")
    public ReviewDto createReview(@PathVariable UUID cardId, @Valid @RequestBody CreateReviewDto createReviewDto) {
        Review review = reviewService.create(cardId, createReviewDto);
        return reviewService.toDto(review);
    }

}
