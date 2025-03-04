package andrehsvictor.memorix.account;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.account.dto.AccountDto;
import andrehsvictor.memorix.account.dto.CreateAccountDto;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AccountDto create(CreateAccountDto createAccountDto) {
        User user = accountMapper.createAccountDtoToUser(createAccountDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userService.save(user);
        return accountMapper.userToAccountDto(user);
    }

}
