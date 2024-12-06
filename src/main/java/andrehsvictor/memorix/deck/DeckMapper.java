package andrehsvictor.memorix.deck;

import org.mapstruct.Mapper;

import andrehsvictor.memorix.deck.dto.GetDeckDto;
import andrehsvictor.memorix.user.UserMapper;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface DeckMapper {

    GetDeckDto deckToGetDeckDto(Deck deck);

}
