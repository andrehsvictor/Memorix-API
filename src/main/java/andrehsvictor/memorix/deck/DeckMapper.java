package andrehsvictor.memorix.deck;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import andrehsvictor.memorix.deck.dto.CreateDeckDto;
import andrehsvictor.memorix.deck.dto.DeckDto;
import andrehsvictor.memorix.deck.dto.UpdateDeckDto;

@Mapper(componentModel = "spring")
public interface DeckMapper {

    DeckDto deckToDeckDto(Deck deck);

    Deck createDeckDtoToDeck(CreateDeckDto createDeckDto);

    Deck updateDeckFromUpdateDeckDto(UpdateDeckDto updateDeckDto, @MappingTarget Deck deck);

    @AfterMapping
    default void afterMapping(UpdateDeckDto updateDeckDto, @MappingTarget Deck deck) {
        if (updateDeckDto.getDescription() != null && updateDeckDto.getDescription().isBlank()) {
            deck.setDescription(null);
        }
        if (updateDeckDto.getCoverUrl() != null && updateDeckDto.getCoverUrl().isBlank()) {
            deck.setCoverUrl(null);
        }
        if (updateDeckDto.getAccentColor() != null && updateDeckDto.getAccentColor().isBlank()) {
            deck.setAccentColor(null);
        }
    }

}
