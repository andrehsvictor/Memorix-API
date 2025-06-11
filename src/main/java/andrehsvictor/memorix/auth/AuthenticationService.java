package andrehsvictor.memorix.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    public Authentication authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        try {
            return authenticationManager.authenticate(authToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid credentials", e);
        } catch (DisabledException e) {
            throw new DisabledException("User must verify their email before logging in", e);
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed", e);
        }
    }
}
