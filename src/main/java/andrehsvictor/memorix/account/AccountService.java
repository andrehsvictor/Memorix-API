package andrehsvictor.memorix.account;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.account.dto.AccountDto;
import andrehsvictor.memorix.account.dto.ResetPasswordDto;
import andrehsvictor.memorix.account.dto.SendActionEmailDto;
import andrehsvictor.memorix.account.dto.TokenDto;
import andrehsvictor.memorix.jwt.JwtService;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;
import andrehsvictor.memorix.user.dto.CreateUserDto;
import andrehsvictor.memorix.user.dto.UpdatePasswordDto;
import andrehsvictor.memorix.user.dto.UpdateUserDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountMapper accountMapper;
    private final UserService userService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;
    private final JwtService jwtService;

    public AccountDto create(CreateUserDto createUserDto) {
        User user = userService.create(createUserDto);
        return accountMapper.userToAccountDto(user);
    }

    public AccountDto get() {
        User user = userService.findById(jwtService.getCurrentUserId());
        return accountMapper.userToAccountDto(user);
    }

    public void delete() {
        userService.delete(jwtService.getCurrentUserId());
    }

    public void sendActionEmail(SendActionEmailDto sendActionEmailDto) {
        SendActionEmailType sendActionEmailType = SendActionEmailType.valueOf(sendActionEmailDto.getType());
        switch (sendActionEmailType) {
            case RESET_PASSWORD:
                passwordResetService.sendPasswordResetEmail(sendActionEmailDto.getEmail(),
                        sendActionEmailDto.getRedirectUrl());
                break;
            case VERIFY_EMAIL:
                emailVerificationService.sendVerificationEmail(sendActionEmailDto.getEmail(),
                        sendActionEmailDto.getRedirectUrl());
                break;
            default:
                throw new IllegalArgumentException("Type is invalid");
        }
    }

    public void verify(TokenDto tokenDto) {
        emailVerificationService.verify(tokenDto.getToken());
    }

    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        passwordResetService.resetPassword(resetPasswordDto.getToken(), resetPasswordDto.getPassword());
    }

    public AccountDto update(UpdateUserDto updateUserDto) {
        User user = userService.update(jwtService.getCurrentUserId(), updateUserDto);
        return accountMapper.userToAccountDto(user);
    }

    public void updatePassword(UpdatePasswordDto updatePasswordDto) {
        userService.updatePassword(jwtService.getCurrentUserId(), updatePasswordDto);
    }

}
