package andrehsvictor.memorix.deck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Data Transfer Object representing a deck with its metadata")
public class DeckDto {
    @Schema(description = "Unique identifier of the deck", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;
    
    @Schema(description = "The name of the deck", example = "Spanish Vocabulary")
    private String name;
    
    @Schema(description = "Description of the deck", example = "Basic Spanish vocabulary for beginners")
    private String description;
    
    @Schema(description = "Cover image URL for the deck", example = "https://example.com/images/spanish-flag.jpg")
    private String coverImageUrl;
    
    @Schema(description = "Theme color for the deck in hex format", example = "#FF5733")
    private String color;
    
    @Schema(description = "Number of cards in the deck", example = "25")
    private Integer cardCount;
    
    @Schema(description = "Creation timestamp in ISO format", example = "2025-06-27T10:30:00Z")
    private String createdAt;
    
    @Schema(description = "Last update timestamp in ISO format", example = "2025-06-27T15:45:00Z")
    private String updatedAt;
}
