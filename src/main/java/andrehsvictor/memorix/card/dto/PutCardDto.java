package andrehsvictor.memorix.card.dto;

import java.util.Set;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutCardDto {

    @Size(min = 1, max = 1000, message = "Question must be between 1 and 1000 characters")
    private String question;

    private Object answer;

    @Size(min = 2, max = 4, message = "Options must have at least 2 elements and at most 4")
    private Set<String> alternatives;

    @Min(value = 0, message = "Correct option index must be between 0 and 3")
    @Max(value = 3, message = "Correct option index must be between 0 and 3")
    private Integer answerIndex;

    private Boolean booleanAnswer;

    @Pattern(regexp = "FLASHCARD|MULTIPLE_CHOICE|BOOLEAN", message = "Type must be FLASHCARD, MULTIPLE_CHOICE or BOOLEAN")
    private String type;
}
