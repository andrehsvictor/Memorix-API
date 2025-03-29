package andrehsvictor.memorix.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.card.Card;
import andrehsvictor.memorix.card.CardService;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.jwt.JwtService;
import andrehsvictor.memorix.progress.ProgressService;
import andrehsvictor.memorix.review.dto.CreateReviewDto;
import andrehsvictor.memorix.review.dto.ReviewDto;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final CardService cardService;
    private final UserService userService;
    private final JwtService jwtService;
    private final ProgressService progressService;

    public ReviewDto toDto(Review review) {
        return reviewMapper.reviewToReviewDto(review);
    }

    public Review findById(Long id) {
        Long userId = jwtService.getCurrentUserId();
        return reviewRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException(Review.class, "ID", id));
    }

    public Page<Review> findAll(String query, Pageable pageable) {
        Long userId = jwtService.getCurrentUserId();
        return reviewRepository.findAllByUserId(query, userId, pageable);
    }

    public Page<Review> findAllByDeckId(Long deckId, Pageable pageable) {
        Long userId = jwtService.getCurrentUserId();
        return reviewRepository.findAllByDeckIdAndUserId(deckId, userId, pageable);
    }

    public Page<Review> findAllByCardId(Long cardId, Pageable pageable) {
        Long userId = jwtService.getCurrentUserId();
        return reviewRepository.findAllByCardIdAndUserId(cardId, userId, pageable);
    }

    public Review create(Long cardId, CreateReviewDto createReviewDto) {
        Review review = reviewMapper.createReviewDtoToReview(createReviewDto);
        Card card = cardService.findById(cardId);
        User user = userService.findMyself();
        review.setUser(user);
        review.setCard(card);
        progressService.progress(user.getId(), card.getId(), createReviewDto);
        return reviewRepository.save(review);
    }

}
