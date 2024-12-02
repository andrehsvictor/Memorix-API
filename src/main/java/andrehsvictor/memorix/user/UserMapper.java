package andrehsvictor.memorix.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import andrehsvictor.memorix.user.dto.GetMeDto;
import andrehsvictor.memorix.user.dto.GetUserDto;
import andrehsvictor.memorix.user.dto.PostUserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    GetUserDto userToGetUserDto(User user);

    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "enabled", constant = "false")
    User postUserDtoToUser(PostUserDto postUserDto);

    GetMeDto userToGetMeDto(User user);

}
