package andrehsvictor.memorix.user;

import org.mapstruct.BeanMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import andrehsvictor.memorix.user.dto.GetMeDto;
import andrehsvictor.memorix.user.dto.GetUserDto;
import andrehsvictor.memorix.user.dto.PostUserDto;
import andrehsvictor.memorix.user.dto.PutUserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    GetMeDto userToGetMeDto(User user);

    @Mapping(target = "enabled", constant = "false")
    @Mapping(target = "emailVerified", constant = "false")
    User postUserDtoToUser(PostUserDto postUserDto);

    GetUserDto userToGetUserDto(User user);

    @Mapping(target = "avatarUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(target = "bio", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User updateUserFromPutUserDto(PutUserDto putUserDto, @MappingTarget User user);

    @BeforeMapping
    default void beforeUpdateUserFromPutUserDto(PutUserDto putUserDto, @MappingTarget User user) {
        if (putUserDto.getEmail() != null) {
            user.setEmailVerified(false);
        }
    }

}
