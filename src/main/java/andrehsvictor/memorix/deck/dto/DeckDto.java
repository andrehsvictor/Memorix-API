package andrehsvictor.memorix.deck.dto;

import andrehsvictor.memorix.user.dto.UserDto;
import lombok.Data;

@Data
public class DeckDto {
    private Long id;
    private String title;
    private String description;
    private String visibility;
    private UserDto author;
    private String coverUrl;
    private String accentColor;
    private Long cardsCount;
    private Long likesCount;
    private boolean liked;
    private String createdAt;
    private String updatedAt;
}
