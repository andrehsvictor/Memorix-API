package andrehsvictor.memorix.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateReviewDto {

    @NotBlank(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    @NotBlank(message = "Time to answer is required")
    @Min(value = 1, message = "Time to answer must be greater than 0")
    private Integer timeToAnswer;

}
