package andrehsvictor.memorix.card.dto;

import lombok.Data;

@Data
public class CardFilterDto {
    private String q;
    private String username;
    private boolean author;
}
