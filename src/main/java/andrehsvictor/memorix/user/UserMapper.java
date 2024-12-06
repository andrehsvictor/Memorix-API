package andrehsvictor.memorix.user;

import org.mapstruct.AfterMapping;
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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User updateUserFromPutUserDto(PutUserDto putUserDto, @MappingTarget User user);

    @BeforeMapping
    default void beforeUpdateUserFromPutUserDto(PutUserDto putUserDto, @MappingTarget User user) {
        if (putUserDto.getEmail() != null && !putUserDto.getEmail().equals(user.getEmail())) {
            user.setEmailVerified(false);
        }
    }

    @AfterMapping
    default void afterUpdateUserFromPutUserDto(PutUserDto putUserDto, @MappingTarget User user) {
        if (putUserDto.getAvatarUrl() != null && putUserDto.getAvatarUrl().isBlank()) {
            user.setAvatarUrl(null);
        }
        if (putUserDto.getBio() != null && putUserDto.getBio().isBlank()) {
            user.setBio(null);
        }
    }

}
