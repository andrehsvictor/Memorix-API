package andrehsvictor.memorix.card.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Data Transfer Object for reviewing a flashcard")
public class ReviewCardDto {

    @Schema(description = "Unique identifier of the card being reviewed", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID cardId;
    
    @Schema(description = "User rating for the flashcard review", example = "4")
    private Integer rating;

}
