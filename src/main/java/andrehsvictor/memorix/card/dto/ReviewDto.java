package andrehsvictor.memorix.card.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {

    @NotNull(message = "Rating is required")
    @Size(min = 0, max = 4, message = "Rating must be between 0 and 4")
    private Integer rating;

    @NotNull(message = "Time to answer is required")
    @Size(min = 0, message = "Time to answer must be greater than 0")
    private Integer timeToAnswer;
    
}
