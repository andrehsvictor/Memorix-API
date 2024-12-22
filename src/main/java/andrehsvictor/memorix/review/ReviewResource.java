package andrehsvictor.memorix.review;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

}
