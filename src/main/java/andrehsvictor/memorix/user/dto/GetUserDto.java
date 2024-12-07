package andrehsvictor.memorix.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserDto {
    private String id;
    private String displayName;
    private String username;
    private String email;
    private boolean emailVerified;
    private boolean enabled;
    private String bio;
    private String avatarUrl;
    private String createdAt;
    private String updatedAt;
}
