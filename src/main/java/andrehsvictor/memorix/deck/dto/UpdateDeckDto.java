package andrehsvictor.memorix.deck.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateDeckDto {

    @Size(max = 255, message = "Name must be at most 255 characters long")
    private String name;

    @Size(max = 1000, message = "Description must be at most 1000 characters long")
    private String description;

    @Pattern(regexp = "^(https?://)?([a-zA-Z0-9.-]+)(:[0-9]+)?(/.*)?$", message = "Cover image URL must be a valid URL")
    @Size(max = 255, message = "Cover image URL must be at most 255 characters long")
    private String coverImageUrl;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Color must be a valid hex color code")
    @Size(min = 4, max = 7, message = "Color must be between 4 and 7 characters long")
    private String color;

}
