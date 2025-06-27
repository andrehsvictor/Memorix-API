package andrehsvictor.memorix.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Data Transfer Object containing statistics about flashcards")
public class CardStatsDto {
    @Schema(description = "Total number of cards", example = "100")
    private Long total;
    
    @Schema(description = "Number of cards due for review", example = "15")
    private Long due;

    @JsonProperty("new")
    @Schema(description = "Number of new cards (never reviewed)", example = "25")
    private Long newCards;

    @Schema(description = "Number of cards currently being learned", example = "10")
    private Long learning;
    
    @Schema(description = "Number of cards that have been reviewed at least once", example = "50")
    private Long reviewed;
}
