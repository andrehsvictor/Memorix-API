package andrehsvictor.memorix.review;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import andrehsvictor.memorix.card.Card;
import andrehsvictor.memorix.card.CardService;
import andrehsvictor.memorix.review.dto.CreateReviewDto;
import andrehsvictor.memorix.review.dto.ReviewDto;

@Mapper(componentModel = "spring")
public abstract class ReviewMapper {

    @Autowired
    protected CardService cardService;

    abstract Review createReviewDtoToReview(CreateReviewDto createReviewDto);

    abstract ReviewDto reviewToReviewDto(Review review);

    @AfterMapping
    protected void afterMapping(Review review, @MappingTarget ReviewDto reviewDto) {
        Card card = cardService.getById(review.getCardId());
        reviewDto.setCard(cardService.toDto(card));
    }

}
