package andrehsvictor.memorix.card.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardStatsDto {
    private Long totalCards;
    private Long dueCards;
    private Long newCards;
    private Long learningCards;
    private Long reviewCards;
}
