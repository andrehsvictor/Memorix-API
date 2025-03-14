package andrehsvictor.memorix.deck.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateDeckVisibilityDto {

    @NotEmpty(message = "Visibility is required")
    @Pattern(regexp = "PUBLIC|PRIVATE", message = "Visibility must be PUBLIC or PRIVATE")
    private String visibility;

}
