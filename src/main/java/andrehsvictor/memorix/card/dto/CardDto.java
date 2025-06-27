package andrehsvictor.memorix.card.dto;

import andrehsvictor.memorix.deck.dto.DeckDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Data Transfer Object representing a flashcard with all its properties")
public class CardDto {
    @Schema(description = "Unique identifier of the card", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;
    
    @Schema(description = "Front content of the flashcard", example = "What is the Spanish word for 'hello'?")
    private String front;
    
    @Schema(description = "Back content of the flashcard (answer)", example = "Hola")
    private String back;
    
    @Schema(description = "Deck that contains this card")
    private DeckDto deck;
    
    @Schema(description = "Spaced repetition ease factor", example = "2.5")
    private Double easeFactor;
    
    @Schema(description = "Current interval in days before next review", example = "3")
    private Integer interval;
    
    @Schema(description = "Number of successful repetitions", example = "2")
    private Integer repetition;
    
    @Schema(description = "Next review due date in ISO format", example = "2025-06-30T10:00:00Z")
    private String due;
    
    @Schema(description = "Total number of times this card has been reviewed", example = "5")
    private Integer reviewCount;
    
    @Schema(description = "Creation timestamp in ISO format", example = "2025-06-27T10:30:00Z")
    private String createdAt;
    
    @Schema(description = "Last update timestamp in ISO format", example = "2025-06-27T15:45:00Z")
    private String updatedAt;
}
