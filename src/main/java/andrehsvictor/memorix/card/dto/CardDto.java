package andrehsvictor.memorix.card.dto;

import andrehsvictor.memorix.deck.dto.DeckDto;
import lombok.Data;

@Data
public class CardDto {

    private Long id;
    private DeckDto deck;
    private String front;
    private String back;
    private String hint;
    private String createdAt;
    private String updatedAt;

}
