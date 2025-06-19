package andrehsvictor.memorix.review.dto;

import andrehsvictor.memorix.card.dto.CardDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewDto {
    private String id;
    private CardDto card;
    private Integer rating;
    private Integer responseTime;
    private String createdAt;
}
