package andrehsvictor.memorix.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final EmailVerificationService emailVerificationService;

    public Authentication authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);
        return authenticationManager.authenticate(authenticationToken);
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
