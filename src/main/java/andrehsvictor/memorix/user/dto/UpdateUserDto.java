package andrehsvictor.memorix.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserDto {

    @Size(max = 50, message = "Display name must be at most 50 characters long")
    private String displayName;

    @Pattern(regexp = "^[a-z0-9_]+$", message = "Username must contain only lowercase letters, numbers, and underscores")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long")
    private String username;

    @Pattern(regexp = "^(https?://)?([a-zA-Z0-9.-]+)(:[0-9]+)?(/.*)?$", message = "Picture URL must be a valid URL")
    @Size(max = 255, message = "Picture URL must be at most 255 characters long")
    private String pictureUrl;

    @Size(max = 500, message = "Bio must be at most 500 characters long")
    private String bio;

}
