package andrehsvictor.memorix.review;

import org.mapstruct.Mapper;

import andrehsvictor.memorix.card.CardMapper;
import andrehsvictor.memorix.review.dto.CreateReviewDto;
import andrehsvictor.memorix.review.dto.ReviewDto;

@Mapper(componentModel = "spring", uses = { CardMapper.class })
public interface ReviewMapper {

    ReviewDto reviewToReviewDto(Review review);

    Review createReviewDtoToReview(CreateReviewDto createReviewDto);

}
