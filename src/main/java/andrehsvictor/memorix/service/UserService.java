package andrehsvictor.memorix.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.entity.User;
import andrehsvictor.memorix.exception.MemorixException;
import andrehsvictor.memorix.repository.UserRepository;
import andrehsvictor.memorix.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private static final String EMAIL_IS_ALREADY_TAKEN = "Email is already taken";
    private static final String USERNAME_IS_ALREADY_TAKEN = "Username is already taken";
    private static final String USER_NOT_FOUND = "User not found with username or email: %s. Please sign up.";
    private static final String USER_NOT_AUTHENTICATED = "User not authenticated. Please sign in.";

    public User activate(User user) {
        user.enable();
        return userRepository.save(user);
    }

    public User create(User user) {
        validateUser(user);
        return userRepository.save(user);
    }

    public void delete(User user) {
        user.delete();
        userRepository.save(user);
    }

    public User restore(User user) {
        user.setDeleted(false);
        return userRepository.save(user);
    }

    public User findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new MemorixException(HttpStatus.NOT_FOUND,
                        String.format(USER_NOT_FOUND, usernameOrEmail)));
    }

    public User findAuthenticatedUser() {
        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return findByUsernameOrEmail(authentication.getName());
    }

    private void validateUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new MemorixException(HttpStatus.BAD_REQUEST, USERNAME_IS_ALREADY_TAKEN);
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new MemorixException(HttpStatus.BAD_REQUEST, EMAIL_IS_ALREADY_TAKEN);
        }
    }
}
