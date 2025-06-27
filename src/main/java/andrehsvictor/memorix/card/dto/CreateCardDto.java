package andrehsvictor.memorix.card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Data Transfer Object for creating a new flashcard")
public class CreateCardDto {

    @NotBlank(message = "Front is required")
    @Size(max = 2000, message = "Front must be at most 2000 characters long")
    @Schema(description = "Front content of the flashcard (question or prompt)", example = "What is the Spanish word for 'hello'?", maxLength = 2000)
    private String front;

    @NotBlank(message = "Back is required")
    @Size(max = 2000, message = "Back must be at most 2000 characters long")
    @Schema(description = "Back content of the flashcard (answer)", example = "Hola", maxLength = 2000)
    private String back;

}
