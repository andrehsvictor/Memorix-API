package andrehsvictor.memorix.review;

import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.common.exception.BadRequestException;
import andrehsvictor.memorix.common.exception.ResourceNotFoundException;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.review.dto.CreateReviewDto;
import andrehsvictor.memorix.review.dto.ReviewDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final JwtService jwtService;

    public ReviewDto toDto(Review review) {
        return reviewMapper.reviewToReviewDto(review);
    }

    public Page<Review> getAll(
            Integer minRating,
            Integer maxRating,
            Integer minResponseTime,
            Integer maxResponseTime,
            Pageable pageable) {
        if (minRating != null && maxRating != null && minRating > maxRating) {
            throw new BadRequestException(
                    "Minimum rating cannot be greater than maximum rating: " + minRating + " > " + maxRating);
        }
        if (minResponseTime != null && maxResponseTime != null && minResponseTime > maxResponseTime) {
            throw new BadRequestException(
                    "Minimum response time cannot be greater than maximum response time: "
                            + minResponseTime + " > " + maxResponseTime);
        }
        UUID userId = jwtService.getCurrentUserUuid();
        return reviewRepository.findAllByUserIdWithFilters(
                userId, minRating, maxRating, minResponseTime, maxResponseTime, pageable);
    }

    public Page<Review> getAllByCardId(UUID cardId, Pageable pageable) {
        UUID userId = jwtService.getCurrentUserUuid();
        return reviewRepository.findAllByCardIdAndUserId(cardId, userId, pageable);
    }

    public Review create(UUID cardId, CreateReviewDto createReviewDto) {
        UUID userId = jwtService.getCurrentUserUuid();
        Review review = reviewMapper.createReviewDtoToReview(createReviewDto);
        review.setCardId(cardId);
        review.setUserId(userId);
        return reviewRepository.save(review);
    }

    public Review getById(UUID id) {
        UUID userId = jwtService.getCurrentUserUuid();
        return reviewRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "ID", id));
    }

    @RabbitListener(queues = "users.v1.delete")
    private void deleteAllByUserId(UUID userId) {
        reviewRepository.deleteByUserId(userId);
    }

    @RabbitListener(queues = "reviews.v1.deleteAllByCardId")
    private void deleteAllByCardId(UUID cardId) {
        reviewRepository.deleteByCardId(cardId);
    }

}
