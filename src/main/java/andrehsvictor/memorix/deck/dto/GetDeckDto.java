package andrehsvictor.memorix.deck.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetDeckDto {

    private String id;
    private String name;
    private String slug;
    private String description;
    private int cardsCount;

}
