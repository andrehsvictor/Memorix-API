package andrehsvictor.memorix.account;

import org.mapstruct.Mapper;

import andrehsvictor.memorix.account.dto.AccountDto;
import andrehsvictor.memorix.user.User;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDto userToAccountDto(User user);

}
