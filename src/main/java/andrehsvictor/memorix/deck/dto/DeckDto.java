package andrehsvictor.memorix.deck.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeckDto {
    private String id;
    private String name;
    private String description;
    private String coverImageUrl;
    private String color;
    private Integer cardCount;
    private String createdAt;
    private String updatedAt;
}
