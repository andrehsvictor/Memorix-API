package andrehsvictor.memorix.user;

import org.mapstruct.Mapper;

import andrehsvictor.memorix.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);
    
}
