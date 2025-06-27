package andrehsvictor.memorix.card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Data Transfer Object for updating an existing flashcard")
public class UpdateCardDto {

    @Size(max = 2000, message = "Front must be at most 2000 characters long")
    @Schema(description = "Updated front content of the flashcard (question or prompt)", example = "What is the Spanish word for 'hello'?", maxLength = 2000)
    private String front;

    @Size(max = 2000, message = "Back must be at most 2000 characters long")
    @Schema(description = "Updated back content of the flashcard (answer)", example = "Hola", maxLength = 2000)
    private String back;

}
