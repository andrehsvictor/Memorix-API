package andrehsvictor.memorix.card;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import andrehsvictor.memorix.card.dto.CardDto;
import andrehsvictor.memorix.card.dto.CreateCardDto;
import andrehsvictor.memorix.card.dto.UpdateCardDto;

@Mapper(componentModel = "spring")
public interface CardMapper {

    CardDto cardToCardDto(Card card);

    Card createCardDtoToCard(CreateCardDto createCardDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Card updateCardFromUpdateCardDto(UpdateCardDto updateCardDto, @MappingTarget Card card);

}
