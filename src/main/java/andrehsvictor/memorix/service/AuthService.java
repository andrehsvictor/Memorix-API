package andrehsvictor.memorix.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    public Authentication authenticate(String usernameOrEmail, String password) {
        Authentication token = new UsernamePasswordAuthenticationToken(usernameOrEmail, password);
        return authenticationManager.authenticate(token);
    }

}
