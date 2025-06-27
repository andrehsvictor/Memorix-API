package andrehsvictor.memorix.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Data Transfer Object for creating a new review session")
public class CreateReviewDto {

    @NotNull(message = "Rating is required")
    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 5, message = "Rating must be at most 5")
    @Schema(description = "User rating for the flashcard (0-5 scale, where 0=Again, 1=Hard, 3=Good, 4=Easy)", example = "4", minimum = "0", maximum = "5")
    private Integer rating;

    @NotNull(message = "Response time is required")
    @Min(value = 1, message = "Response time must be at least 1")
    @Schema(description = "Time taken to respond in milliseconds", example = "3500", minimum = "1")
    private Integer responseTime;

}
