package andrehsvictor.memorix.review;

import org.mapstruct.Mapper;

import andrehsvictor.memorix.review.dto.GetReviewDto;
import andrehsvictor.memorix.review.dto.PostReviewDto;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    Review postReviewDtoToReview(PostReviewDto postReviewDto);

    GetReviewDto reviewToGetReviewDto(Review review);

}
