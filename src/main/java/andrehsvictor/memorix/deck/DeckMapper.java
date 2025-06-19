package andrehsvictor.memorix.deck;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import andrehsvictor.memorix.deck.dto.CreateDeckDto;
import andrehsvictor.memorix.deck.dto.DeckDto;
import andrehsvictor.memorix.deck.dto.UpdateDeckDto;

@Mapper(componentModel = "spring")
public interface DeckMapper {

    DeckDto deckToDeckDto(Deck deck);

    Deck createDeckDtoToDeck(CreateDeckDto createDeckDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Deck updateDeckFromUpdateDeckDto(UpdateDeckDto updateDeckDto, @MappingTarget Deck deck);

    @AfterMapping
    default void afterMapping(
            @MappingTarget Deck deck,
            UpdateDeckDto updateDeckDto) {
        if (updateDeckDto.getCoverImageUrl() != null && updateDeckDto.getCoverImageUrl().isBlank()) {
            deck.setCoverImageUrl(null);
        }
        if (updateDeckDto.getDescription() != null && updateDeckDto.getDescription().isBlank()) {
            deck.setDescription(null);
        }
    }

}
