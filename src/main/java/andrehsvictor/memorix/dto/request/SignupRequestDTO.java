package andrehsvictor.memorix.dto.request;

import andrehsvictor.memorix.entity.User;
import lombok.Data;

@Data
public class SignupRequestDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String avatarUrl;

    public User toUser() {
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email)
                .password(password)
                .avatarUrl(avatarUrl)
                .build();
    }
}
