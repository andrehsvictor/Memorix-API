package andrehsvictor.memorix.deck;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import andrehsvictor.memorix.deck.dto.CreateDeckDto;
import andrehsvictor.memorix.deck.dto.DeckDto;
import andrehsvictor.memorix.deck.dto.UpdateDeckDto;
import andrehsvictor.memorix.user.UserService;

@Mapper(componentModel = "spring")
public abstract class DeckMapper {

    @Autowired
    protected UserService userService;

    @Autowired
    protected DeckService deckService;

    @Mapping(target = "liked", expression = "java(deckService.isLikedByCurrentUser(deck.getId()))")
    public abstract DeckDto deckToDeckDto(Deck deck);

    public abstract Deck createDeckDtoToDeck(CreateDeckDto createDeckDto);

    public abstract Deck updateDeckFromUpdateDeckDto(UpdateDeckDto updateDeckDto, @MappingTarget Deck deck);

    @AfterMapping
    public void afterMapping(UpdateDeckDto updateDeckDto, @MappingTarget Deck deck) {
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
