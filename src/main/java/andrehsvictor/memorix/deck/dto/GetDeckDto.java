package andrehsvictor.memorix.deck.dto;

import andrehsvictor.memorix.user.dto.GetUserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetDeckDto {
    private String id;
    private String name;
    private String description;
    private GetUserDto owner;
    private String visibility;
    private String coverUrl;
    private String accentColor;
    private int usersCount;
    private int cardsCount;
    private String createdAt;
}
