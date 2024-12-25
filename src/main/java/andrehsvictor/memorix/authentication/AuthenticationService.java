package andrehsvictor.memorix.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService;

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

    public void sendVerificationEmail(String email) {
        emailVerificationService.sendVerificationEmail(email);
    }

    public void sendPasswordResetEmail(String email) {

    }

    public void resetPassword(String token, String newPassword) {

    }

    public void verifyEmail(String token) {
        emailVerificationService.verifyEmail(token);
    }
}
