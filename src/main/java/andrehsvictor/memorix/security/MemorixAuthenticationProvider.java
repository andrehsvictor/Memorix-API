package andrehsvictor.memorix.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import andrehsvictor.memorix.entity.User;
import andrehsvictor.memorix.exception.MemorixException;
import andrehsvictor.memorix.service.UserService;

@Component
public class MemorixAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String INVALID_CREDENTIALS = "Invalid credentials. Please try again.";
    private static final String USER_NOT_ENABLED = "User is not enabled. Please contact the administrator.";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String usernameOrEmail = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        User user = userService.findByUsernameOrEmail(usernameOrEmail);
        validateCredentials(password, user);
        validateUserEnabled(user);

        UserDetails userDetails = new UserDetailsImpl(user);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private void validateUserEnabled(User user) {
        if (!user.isEnabled()) {
            throw new MemorixException(HttpStatus.UNAUTHORIZED, USER_NOT_ENABLED);
        }
    }

    private void validateCredentials(String password, User user) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new MemorixException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    
}
