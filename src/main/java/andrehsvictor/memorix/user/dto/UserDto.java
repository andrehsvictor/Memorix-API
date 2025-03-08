package andrehsvictor.memorix.user.dto;

import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String displayName;
    private String username;
    private String pictureUrl;
    private String bio;
    private String createdAt;

}
