package andrehsvictor.memorix.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import andrehsvictor.memorix.user.dto.GetMeDto;
import andrehsvictor.memorix.user.dto.PostUserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    GetMeDto userToGetMeDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", constant = "false")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User postUserDtoToUser(PostUserDto postUserDto);

}
