package andrehsvictor.memorix.user;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;
}
