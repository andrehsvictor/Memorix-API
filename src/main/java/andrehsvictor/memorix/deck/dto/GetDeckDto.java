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
    private Long cardsCount;
    private String coverUrl;
    private String accentColor;
    private String createdAt;
    private String updatedAt;

}
