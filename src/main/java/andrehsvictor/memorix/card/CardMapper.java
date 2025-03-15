package andrehsvictor.memorix.card;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import andrehsvictor.memorix.card.dto.CardDto;
import andrehsvictor.memorix.card.dto.CreateCardDto;
import andrehsvictor.memorix.card.dto.UpdateCardDto;
import andrehsvictor.memorix.deck.DeckMapper;

@Mapper(componentModel = "spring", uses = { DeckMapper.class })
public interface CardMapper {

    CardDto cardToCardDto(Card card);

    Card createCardDtoToCard(CreateCardDto createCardDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Card updateCardFromUpdateCardDto(UpdateCardDto updateCardDto, @MappingTarget Card card);

    default void afterUpdate(@MappingTarget Card card, UpdateCardDto updateCardDto) {
        if (updateCardDto.getHint() != null && !updateCardDto.getHint().isBlank()) {
            card.setHint(null);
        }
    }

}
