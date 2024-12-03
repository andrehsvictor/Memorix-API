package andrehsvictor.memorix.user;

import org.mapstruct.Mapper;

import andrehsvictor.memorix.user.dto.GetMeDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    GetMeDto userToGetMeDto(User user);

}
