package andrehsvictor.memorix.review;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.review.dto.CreateReviewDto;
import andrehsvictor.memorix.review.dto.ReviewDto;
import andrehsvictor.memorix.util.StringUtil;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/api/v1/reviews")
    public Page<ReviewDto> findAll(
            @RequestParam(required = false, name = "q") String query,
            Pageable pageable) {
        query = StringUtil.normalize(query);
        return reviewService.findAll(query, pageable)
                .map(reviewService::toDto);
    }

    @GetMapping("/api/v1/cards/{cardId}/reviews")
    public Page<ReviewDto> findAllByCardId(
            @PathVariable Long cardId,
            Pageable pageable) {
        return reviewService.findAllByCardId(cardId, pageable)
                .map(reviewService::toDto);
    }

    @GetMapping("/api/v1/decks/{deckId}/reviews")
    public Page<ReviewDto> findAllByDeckId(
            @PathVariable Long deckId,
            Pageable pageable) {
        return reviewService.findAllByDeckId(deckId, pageable)
                .map(reviewService::toDto);
    }

    @GetMapping("/api/v1/reviews/{id}")
    public ReviewDto findById(@PathVariable Long id) {
        return reviewService.toDto(reviewService.findById(id));
    }

    @PostMapping("/api/v1/cards/{cardId}/reviews")
    public ResponseEntity<ReviewDto> create(
            @PathVariable Long cardId,
            @Valid @RequestBody CreateReviewDto createReviewDto) {
        Review review = reviewService.create(cardId, createReviewDto);
        URI location = URI.create(String.format("/api/v1/reviews/%d", review.getId()));
        return ResponseEntity.created(location).body(reviewService.toDto(review));
    }

    

}
