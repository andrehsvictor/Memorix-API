package andrehsvictor.memorix.user.dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String username;
    private String displayName;
    private String pictureUrl;
    private String bio;
    private String createdAt;
}
