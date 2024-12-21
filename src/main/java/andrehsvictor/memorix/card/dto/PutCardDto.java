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

    @Size(min = 1, max = 1000, message = "Answer must be between 1 and 1000 characters")
    private String answer;

    @Min(value = 2, message = "Options must have at least 2 elements")
    @Max(value = 4, message = "Options must have at most 4 elements")
    private Set<String> options;

    @Min(value = 0, message = "Correct option index must be between 0 and 3")
    @Max(value = 3, message = "Correct option index must be between 0 and 3")
    private Integer correctOptionIndex;

    private Boolean booleanAnswer;

    @Pattern(regexp = "FLASHCARD|MULTIPLE_CHOICE|BOOLEAN", message = "Type must be FLASHCARD, MULTIPLE_CHOICE or BOOLEAN")
    private String type;
}
