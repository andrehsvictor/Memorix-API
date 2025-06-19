package andrehsvictor.memorix.card.dto;

import andrehsvictor.memorix.deck.dto.DeckDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardDto {
    private String id;
    private String front;
    private String back;
    private DeckDto deck;
    private Double easeFactor;
    private Integer interval;
    private Integer repetition;
    private String due;
    private Integer reviewCount;
    private String createdAt;
    private String updatedAt;
}
