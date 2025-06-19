package andrehsvictor.memorix.card.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardDto {
    private String id;
    private String front;
    private String back;
    private String deckId;
    private Double easeFactor;
    private Integer interval;
    private Integer repetition;
    private String due;
    private Integer reviewCount;
    private String createdAt;
    private String updatedAt;
}
