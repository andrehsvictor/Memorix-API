package andrehsvictor.memorix.card.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewCardDto {

    private UUID cardId;
    private Integer rating;

}
