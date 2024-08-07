package andrehsvictor.memorix.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private static final String USER_NOT_FOUND = "User not found with username or email: %s. Please sign up.";
    private static final String USER_NOT_AUTHENTICATED = "User not authenticated. Please sign in.";

    public User findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new MemorixException(HttpStatus.NOT_FOUND,
                        String.format(USER_NOT_FOUND, usernameOrEmail)));
    }

    public User findAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetailsImpl)) {
            throw new MemorixException(HttpStatus.UNAUTHORIZED, USER_NOT_AUTHENTICATED);
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        return userDetails.getUser();
    }
}
