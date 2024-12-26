package andrehsvictor.memorix.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.authentication.dto.ActionEmailDto;
import andrehsvictor.memorix.authentication.dto.ResetPasswordDto;
import andrehsvictor.memorix.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService;
    private final ResetPasswordService resetPasswordService;

    public Authentication authenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email,
                password);
        try {
            return authenticationManager.authenticate(authenticationToken);
        } catch (DisabledException e) {
            throw new UnauthorizedException("User account is not fully activated");
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid e-mail or password");
        } catch (Exception e) {
            throw new UnauthorizedException("An error occurred while trying to authenticate the user");
        }
    }

    public void sendActionEmail(ActionEmailDto actionEmailDto) {
        String email = actionEmailDto.getEmail();
        String action = actionEmailDto.getAction();
        switch (action) {
            case "VERIFY_EMAIL":
                emailVerificationService.sendVerificationEmail(email);
                break;
            case "RESET_PASSWORD":
                resetPasswordService.sendResetPasswordEmail(email);
                break;
            default:
                throw new IllegalArgumentException("Invalid action");
        }
    }

    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        resetPasswordService.resetPassword(resetPasswordDto);
    }

    public void verifyEmail(String token) {
        emailVerificationService.verifyEmail(token);
    }
}
