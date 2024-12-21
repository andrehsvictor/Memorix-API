package andrehsvictor.memorix.card;

import org.mapstruct.Mapper;

import andrehsvictor.memorix.card.dto.PostCardDto;

@Mapper(componentModel = "spring")
public interface CardMapper {

    Card postCardDtoToCard(PostCardDto postCardDto);
    
}
