package andrehsvictor.memorix.user;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import andrehsvictor.memorix.user.dto.CreateUserDto;
import andrehsvictor.memorix.user.dto.MeDto;
import andrehsvictor.memorix.user.dto.UpdateUserDto;
import andrehsvictor.memorix.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);

    MeDto userToMeDto(User user);

    User createUserDtoToUser(CreateUserDto createUserDto);

    User updateUserFromUpdateUserDto(UpdateUserDto updateUserDto, @MappingTarget User user);

    @AfterMapping
    default void AfterMapping(
            @MappingTarget User user,
            UpdateUserDto updateUserDto) {
        if (updateUserDto.getPictureUrl() != null && updateUserDto.getPictureUrl().isBlank()) {
            user.setPictureUrl(null);
        }
        if (updateUserDto.getBio() != null && updateUserDto.getBio().isBlank()) {
            user.setBio(null);
        }
    }

}
