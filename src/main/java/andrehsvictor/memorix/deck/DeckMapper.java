package andrehsvictor.memorix.deck;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import andrehsvictor.memorix.deck.dto.GetDeckDto;
import andrehsvictor.memorix.deck.dto.PostDeckDto;
import andrehsvictor.memorix.deck.dto.PutDeckDto;

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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Deck updateDeckFromPutDeckDto(PutDeckDto putDeckDto, @MappingTarget Deck deck);

    @AfterMapping
    protected void afterMapping(PutDeckDto putDeckDto, @MappingTarget Deck deck) {
        if (deck.getDescription() != null && deck.getDescription().isBlank()) {
            deck.setDescription(null);
        }
        if (deck.getCoverUrl() != null && deck.getCoverUrl().isBlank()) {
            deck.setCoverUrl(null);
        }
        deck.setSlug(deckService.generateSlug(deck.getName()));
    }

}
