package andrehsvictor.memorix.deck;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import andrehsvictor.memorix.deck.dto.GetDeckDto;
import andrehsvictor.memorix.deck.dto.PostDeckDto;
import andrehsvictor.memorix.deck.dto.PutDeckDto;

@Mapper(componentModel = "spring")
public interface DeckMapper {

    GetDeckDto deckToGetDeckDto(Deck deck);

    @Mapping(target = "name", expression = "java(postDeckDto.getName().trim())")
    Deck postDeckDtoToDeck(PostDeckDto postDeckDto);

    @AfterMapping
    default void afterMapping(@MappingTarget Deck deck, PostDeckDto postDeckDto) {
        if (postDeckDto.getAccentColor() == null) {
            String randomColor = "#" + Integer.toHexString((int) (Math.random() * 0xffffff));
            deck.setAccentColor(randomColor);
        }
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Deck updateDeckFromPutDeckDto(PutDeckDto putDeckDto, @MappingTarget Deck deck);

    @AfterMapping
    default void afterMapping(@MappingTarget Deck deck, PutDeckDto putDeckDto) {
        if (putDeckDto.getCoverUrl() != null && putDeckDto.getCoverUrl().isBlank()) {
            deck.setCoverUrl(null);
        }
        if (putDeckDto.getDescription() != null && putDeckDto.getDescription().isBlank()) {
            deck.setDescription(null);
        }
    }

}
