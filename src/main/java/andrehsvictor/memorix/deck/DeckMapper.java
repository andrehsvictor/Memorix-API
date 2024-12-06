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
import andrehsvictor.memorix.deckuser.DeckUserService;
import andrehsvictor.memorix.user.UserMapper;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public abstract class DeckMapper {

    @Autowired
    protected DeckService deckService;

    @Autowired
    protected DeckUserService deckUserService;

    public abstract GetDeckDto deckToGetDeckDto(Deck deck);

    @Mapping(target = "accentColor", expression = "java(deckService.generateRandomAccentColor())")
    public abstract Deck postDeckDtoToDeck(PostDeckDto postDeckDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract Deck updateDeckFromPutDeckDto(PutDeckDto putDeckDto, @MappingTarget Deck deck);

    @AfterMapping
    protected void afterMapping(PutDeckDto putDeckDto, @MappingTarget Deck deck) {
        if (putDeckDto.getVisibility().equals("PRIVATE") && !deck.getVisibility().equals(deck.getVisibility())) {
            deckUserService.deleteAllByDeckIdExceptOwner(deck.getId());
            deck.setUsersCount(1);
        }
        if (putDeckDto.getCoverUrl().isBlank()) {
            deck.setCoverUrl(null);
        }
        if (putDeckDto.getDescription().isBlank()) {
            deck.setDescription(null);
        }
    }

}
