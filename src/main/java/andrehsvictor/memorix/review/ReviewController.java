package andrehsvictor.memorix.review;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.review.dto.CreateReviewDto;
import andrehsvictor.memorix.review.dto.ReviewDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Review management and study statistics endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(
        summary = "Get all reviews", 
        description = "Retrieve a paginated list of all user's reviews with optional filters"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Reviews retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping("/api/v1/reviews")
    public Page<ReviewDto> getAllReviews(
            @Parameter(description = "Minimum rating filter (1-5)") 
            @RequestParam(required = false) Integer minRating,
            @Parameter(description = "Maximum rating filter (1-5)") 
            @RequestParam(required = false) Integer maxRating,
            @Parameter(description = "Minimum response time filter (in milliseconds)") 
            @RequestParam(required = false) Integer minResponseTime,
            @Parameter(description = "Maximum response time filter (in milliseconds)") 
            @RequestParam(required = false) Integer maxResponseTime,
            @Parameter(description = "Pagination parameters") 
            Pageable pageable) {
        Page<Review> reviews = reviewService.getAll(minRating, maxRating, minResponseTime, maxResponseTime, pageable);
        return reviews.map(reviewService::toDto);
    }

    @Operation(
        summary = "Get reviews by card", 
        description = "Retrieve a paginated list of reviews for a specific card"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Card reviews retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @GetMapping("/api/v1/cards/{cardId}/reviews")
    public Page<ReviewDto> getReviewsByCardId(
            @Parameter(description = "Card unique identifier") 
            @PathVariable UUID cardId, 
            @Parameter(description = "Pagination parameters") 
            Pageable pageable) {
        Page<Review> reviews = reviewService.getAllByCardId(cardId, pageable);
        return reviews.map(reviewService::toDto);
    }

    @Operation(
        summary = "Create review", 
        description = "Create a new review for a card after study session"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Review created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid review data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @PostMapping("/api/v1/cards/{cardId}/reviews")
    public ReviewDto createReview(
            @Parameter(description = "Card unique identifier") 
            @PathVariable UUID cardId, 
            @Parameter(description = "Review data with rating and response time") 
            @Valid @RequestBody CreateReviewDto createReviewDto) {
        Review review = reviewService.create(cardId, createReviewDto);
        return reviewService.toDto(review);
    }

}
