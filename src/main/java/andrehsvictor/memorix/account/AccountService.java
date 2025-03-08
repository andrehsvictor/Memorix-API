package andrehsvictor.memorix.account;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.account.dto.AccountDto;
import andrehsvictor.memorix.account.dto.SendActionEmailDto;
import andrehsvictor.memorix.account.dto.TokenDto;
import andrehsvictor.memorix.jwt.JwtService;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;
import andrehsvictor.memorix.user.dto.CreateUserDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountMapper accountMapper;
    private final UserService userService;
    private final EmailVerifier emailVerifier;
    private final JwtService jwtService;

    public AccountDto create(CreateUserDto createUserDto) {
        User user = userService.create(createUserDto);
        return accountMapper.userToAccountDto(user);
    }

    public AccountDto get() {
        User user = userService.getById(jwtService.getCurrentUserId());
        return accountMapper.userToAccountDto(user);
    }

    public void sendActionEmail(SendActionEmailDto sendActionEmailDto) {
        SendActionEmailType sendActionEmailType = SendActionEmailType.valueOf(sendActionEmailDto.getType());
        switch (sendActionEmailType) {
            case RESET_PASSWORD:
                throw new UnsupportedOperationException("Not implemented yet");
            case VERIFY_EMAIL:
                emailVerifier.sendVerificationEmail(sendActionEmailDto.getEmail(), sendActionEmailDto.getRedirectUrl());
                break;
            default:
                throw new IllegalArgumentException("Type is invalid");
        }
    }

    public void verify(TokenDto tokenDto) {
        emailVerifier.verify(tokenDto.getToken());
    }

}
