package andrehsvictor.memorix.deckuser;

import org.mapstruct.Mapper;

import andrehsvictor.memorix.deckuser.dto.DeckUserDto;
import andrehsvictor.memorix.user.UserMapper;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface DeckUserMapper {

    DeckUserDto deckUserToDeckUserDto(DeckUser deckUser);

}
