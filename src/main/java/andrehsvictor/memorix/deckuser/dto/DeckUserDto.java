package andrehsvictor.memorix.deckuser.dto;

import andrehsvictor.memorix.user.dto.UserDto;
import lombok.Data;

@Data
public class DeckUserDto {
    private UserDto user;
    private String accessLevel;
    private String createdAt;
    private String updatedAt;
}
