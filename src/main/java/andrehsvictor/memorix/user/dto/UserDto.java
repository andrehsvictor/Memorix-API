package andrehsvictor.memorix.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private Long id;
    private String displayName;
    private String username;
    private String pictureUrl;
    private String bio;
    private String createdAt;
    
}
