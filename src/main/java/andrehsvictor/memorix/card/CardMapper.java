package andrehsvictor.memorix.card;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import andrehsvictor.memorix.card.dto.PostCardDto;
import andrehsvictor.memorix.card.dto.PutCardDto;

@Mapper(componentModel = "spring")
public interface CardMapper {

    Card postCardDtoToCard(PostCardDto postCardDto);

    Card updateCardDtoFromPutCardDto(PutCardDto putCardDto, @MappingTarget Card card);

}
