package andrehsvictor.memorix.deck;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import andrehsvictor.memorix.deck.dto.GetDeckDto;
import andrehsvictor.memorix.deck.dto.PostDeckDto;

@Mapper(componentModel = "spring")
public abstract class DeckMapper {

    @Autowired
    protected DeckService deckService;

    @Mapping(target = "slug", expression = "java(deckService.generateSlug(postDeckDto.getName()))")
    public abstract Deck postDeckDtoToDeck(PostDeckDto postDeckDto);

    @AfterMapping
    protected void afterMapping(PostDeckDto postDeckDto, @MappingTarget Deck deck) {
        if (deck.getAccentColor() == null) {
            deck.setAccentColor(deckService.generateAccentColor());
        }
    }

    public abstract GetDeckDto deckToGetDeckDto(Deck deck);

}
