package andrehsvictor.memorix.card.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {

    @NotNull(message = "Rating is required")
    @Min(value = 0, message = "Rating must be greater or equal to 0")
    @Max(value = 4, message = "Rating must be less or equal to 4")
    private Integer rating;

    @NotNull(message = "Time to answer is required")
    @Min(value = 0, message = "Time to answer must be greater or equal to 0")
    private Integer timeToAnswer;

}
