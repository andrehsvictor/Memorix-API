package andrehsvictor.memorix.deck;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import andrehsvictor.memorix.deck.dto.GetDeckDto;
import andrehsvictor.memorix.deck.dto.PostDeckDto;
import andrehsvictor.memorix.deck.dto.PutDeckDto;

@Mapper(componentModel = "spring")
public interface DeckMapper {

    Deck postDeckDtoToDeck(PostDeckDto postDeckDto);

    GetDeckDto deckToGetDeckDto(Deck deck);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Deck updateDeckFromPutDeckDto(PutDeckDto putDeckDto, @MappingTarget Deck deck);

    @AfterMapping
    default void afterMapping(PutDeckDto putDeckDto, @MappingTarget Deck deck) {
        if (deck.getDescription() != null && deck.getDescription().isBlank()) {
            deck.setDescription(null);
        }
        if (deck.getCoverUrl() != null && deck.getCoverUrl().isBlank()) {
            deck.setCoverUrl(null);
        }
    }

}
