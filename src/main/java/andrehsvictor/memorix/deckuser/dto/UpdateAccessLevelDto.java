package andrehsvictor.memorix.deckuser.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateAccessLevelDto {

    @NotEmpty(message = "Access level is required")
    @Pattern(regexp = "^(EDITOR|VIEWER)$", message = "Invalid access level")
    private String accessLevel;
    
}
