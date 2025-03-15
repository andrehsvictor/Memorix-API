package andrehsvictor.memorix.card.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCardDto {

    @NotEmpty(message = "Front is required")
    @Size(max = 255, message = "Front must have a maximum of 255 characters")
    private String front;

    @NotEmpty(message = "Back is required")
    @Size(max = 255, message = "Back must have a maximum of 255 characters")
    private String back;

    @Size(max = 255, message = "Hint must have a maximum of 255 characters")
    private String hint;

}
