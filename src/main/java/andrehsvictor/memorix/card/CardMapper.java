package andrehsvictor.memorix.card;

import java.util.UUID;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import andrehsvictor.memorix.card.dto.CardDto;
import andrehsvictor.memorix.card.dto.CreateCardDto;
import andrehsvictor.memorix.card.dto.UpdateCardDto;
import andrehsvictor.memorix.deck.Deck;
import andrehsvictor.memorix.deck.DeckService;
import andrehsvictor.memorix.deck.dto.DeckDto;

@Mapper(componentModel = "spring")
public abstract class CardMapper {

    @Autowired
    protected DeckService deckService;

    @Mapping(target = "deck", ignore = true)
    abstract CardDto cardToCardDto(Card card);

    abstract Card createCardDtoToCard(CreateCardDto createCardDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    abstract Card updateCardFromUpdateCardDto(UpdateCardDto updateCardDto, @MappingTarget Card card);

    @AfterMapping
    protected void afterMapping(Card card, @MappingTarget CardDto cardDto) {
        Deck deck = deckService.getById(card.getDeckId());
        DeckDto deckDto = deckService.toDto(deck);
        cardDto.setDeck(deckDto);
    }

}
