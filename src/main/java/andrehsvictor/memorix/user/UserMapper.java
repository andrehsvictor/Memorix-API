package andrehsvictor.memorix.user;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import andrehsvictor.memorix.user.dto.GetUserDto;
import andrehsvictor.memorix.user.dto.PostUserDto;
import andrehsvictor.memorix.user.dto.PutUserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User postUserDtoToUser(PostUserDto postUserDto);

    GetUserDto userToGetUserDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User updateUserFromPutUserDto(PutUserDto putUserDto, @MappingTarget User user);

    @AfterMapping
    default void afterMapping(PutUserDto putUserDto, @MappingTarget User user) {
        if (putUserDto.getAvatarUrl() != null && putUserDto.getAvatarUrl().isBlank()) {
            user.setAvatarUrl(null);
        }
    }

}
