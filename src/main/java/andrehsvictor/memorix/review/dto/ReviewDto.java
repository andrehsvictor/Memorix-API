package andrehsvictor.memorix.review.dto;

import andrehsvictor.memorix.card.dto.CardDto;
import lombok.Data;

@Data
public class ReviewDto {

    private CardDto card;
    private Integer rating;
    private Integer timeToAnswer;
    private boolean correct;
    private String createdAt;

}
