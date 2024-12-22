package andrehsvictor.memorix.review.dto;

import java.time.LocalDateTime;

import andrehsvictor.memorix.card.dto.GetCardDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetReviewDto {

    private String id;
    private GetCardDto card;
    private Integer rating;
    private Integer timeToAnswer;
    private LocalDateTime createdAt;

}
