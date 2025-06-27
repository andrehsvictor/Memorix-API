package andrehsvictor.memorix.review.dto;

import andrehsvictor.memorix.card.dto.CardDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Data Transfer Object representing a card review session")
public class ReviewDto {
    @Schema(description = "Unique identifier of the review", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;
    
    @Schema(description = "The card that was reviewed")
    private CardDto card;
    
    @Schema(description = "Rating given during the review (1-5 scale)", example = "4")
    private Integer rating;
    
    @Schema(description = "Response time in milliseconds", example = "3500")
    private Integer responseTime;
    
    @Schema(description = "Review session timestamp in ISO format", example = "2025-06-27T10:30:00Z")
    private String createdAt;
}
