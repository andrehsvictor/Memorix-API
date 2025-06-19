package andrehsvictor.memorix.review;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.common.exception.ResourceNotFoundException;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.review.dto.CreateReviewDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final JwtService jwtService;

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

}
