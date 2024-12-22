package andrehsvictor.memorix.review;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.review.dto.GetReviewDto;
import andrehsvictor.memorix.review.dto.PostReviewDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReviewResource {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @PostMapping("/v1/cards/{id}/reviews")
    public ResponseEntity<GetReviewDto> create(@PathVariable UUID cardId,
            @RequestBody @Valid PostReviewDto postReviewDto,
            @AuthenticationPrincipal UUID userId) {
        Review review = reviewService.create(postReviewDto, userId, cardId);
        GetReviewDto getReviewDto = reviewMapper.reviewToGetReviewDto(review);
        URI location = URI.create("/v1/reviews/" + getReviewDto.getId());
        return ResponseEntity.created(location).body(getReviewDto);
    }

    @GetMapping("/v1/reviews")
    public ResponseEntity<Page<GetReviewDto>> getAll(@AuthenticationPrincipal UUID userId, Pageable pageable) {
        Page<Review> reviews = reviewService.getAllByUserId(userId, pageable);
        Page<GetReviewDto> getReviewDtos = reviews.map(reviewMapper::reviewToGetReviewDto);
        return ResponseEntity.ok(getReviewDtos);
    }

    @GetMapping("/v1/cards/{id}/reviews")
    public ResponseEntity<Page<GetReviewDto>> getAllByCardId(@PathVariable UUID cardId,
            @AuthenticationPrincipal UUID userId,
            Pageable pageable) {
        Page<Review> reviews = reviewService.getAllByUserIdAndCardId(userId, cardId, pageable);
        Page<GetReviewDto> getReviewDtos = reviews.map(reviewMapper::reviewToGetReviewDto);
        return ResponseEntity.ok(getReviewDtos);
    }

    @GetMapping("/v1/decks/{slug}/reviews")
    public ResponseEntity<Page<GetReviewDto>> getAllByDeckSlug(@PathVariable String slug,
            @AuthenticationPrincipal UUID userId,
            Pageable pageable) {
        Page<Review> reviews = reviewService.getAllByUserIdAndDeckSlug(userId, slug, pageable);
        Page<GetReviewDto> getReviewDtos = reviews.map(reviewMapper::reviewToGetReviewDto);
        return ResponseEntity.ok(getReviewDtos);
    }

}
