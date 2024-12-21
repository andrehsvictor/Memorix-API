package andrehsvictor.memorix.card.dto;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCardDto {

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "FLASHCARD|MULTIPLE_CHOICE|BOOLEAN", message = "Type must be FLASHCARD, MULTIPLE_CHOICE or BOOLEAN")
    private String type;

    @Size(min = 1, max = 1000, message = "Question must be between 1 and 1000 characters")
    @NotBlank(message = "Question is required")
    private String question;

    private String answer;

    private Boolean correct;

    @Size(min = 2, max = 4, message = "Options must have at least 2 elements and at most 4")
    private List<String> alternatives;

    @Min(value = 0, message = "Answer index must be between 0 and 3")
    @Max(value = 3, message = "Answer index must be between 0 and 3")
    private Integer answerIndex;

}
