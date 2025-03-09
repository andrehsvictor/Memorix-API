package andrehsvictor.memorix.user;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import andrehsvictor.memorix.user.dto.CreateUserDto;
import andrehsvictor.memorix.user.dto.UpdateUserDto;
import andrehsvictor.memorix.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);

    User createUserDtoToUser(CreateUserDto createUserDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User updateUserFromUpdateUserDto(UpdateUserDto updateUserDto, @MappingTarget User user);

    default void afterMapping(UpdateUserDto updateUserDto, @MappingTarget User user) {
        if (updateUserDto.getBio() != null && updateUserDto.getBio().isBlank()) {
            user.setBio(null);
        }

        if (updateUserDto.getPictureUrl() != null && updateUserDto.getPictureUrl().isBlank()) {
            user.setPictureUrl(null);
        }
    }

}
