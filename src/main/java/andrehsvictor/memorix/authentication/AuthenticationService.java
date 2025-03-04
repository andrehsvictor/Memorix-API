package andrehsvictor.memorix.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    public Authentication authenticate(String username, String password) {
        try {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,
                    password);
            return authenticationManager.authenticate(authentication);
        } catch (Exception e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }
}
