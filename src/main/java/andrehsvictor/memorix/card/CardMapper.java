package andrehsvictor.memorix.card;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import andrehsvictor.memorix.card.dto.GetCardDto;
import andrehsvictor.memorix.card.dto.PostCardDto;
import andrehsvictor.memorix.card.dto.PutCardDto;
import andrehsvictor.memorix.deck.DeckMapper;

@Mapper(componentModel = "spring", uses = { DeckMapper.class }, imports = { CardType.class })
public interface CardMapper {

    Card postCardDtoToCard(PostCardDto postCardDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Card updateCardDtoFromPutCardDto(PutCardDto putCardDto, @MappingTarget Card card);

    @Mapping(target = "answer", ignore = true)
    GetCardDto cardToGetCardDto(Card card);

    @AfterMapping
    default void afterMapping(@MappingTarget GetCardDto getCardDto, Card card) {
        switch (card.getType()) {
            case BOOLEAN:
                break;
            case MULTIPLE_CHOICE:
                getCardDto.setAnswer(card.getAlternatives().toArray()[card.getAnswerIndex()].toString());
                break;
            case FLASHCARD:
                getCardDto.setAnswer(card.getAnswer());
                break;
        }
    }

}
