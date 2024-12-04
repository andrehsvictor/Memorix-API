package andrehsvictor.memorix.user;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserQuery {

    @Size(min = 1, message = "Display name must have at least 1 character")
    private String displayName;

    @Size(min = 1, message = "Username must have at least 1 character")
    private String username;
    
}
