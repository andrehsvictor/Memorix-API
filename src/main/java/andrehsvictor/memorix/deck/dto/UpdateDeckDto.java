package andrehsvictor.memorix.deck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Data Transfer Object for updating an existing deck")
public class UpdateDeckDto {

    @Size(max = 255, message = "Name must be at most 255 characters long")
    @Schema(description = "Updated name of the deck", example = "Advanced Spanish Vocabulary", maxLength = 255)
    private String name;

    @Size(max = 1000, message = "Description must be at most 1000 characters long")
    @Schema(description = "Updated description of the deck", example = "Advanced Spanish vocabulary for intermediate learners", maxLength = 1000)
    private String description;

    @Pattern(regexp = "^(https?://)?([a-zA-Z0-9.-]+)(:[0-9]+)?(/.*)?$", message = "Cover image URL must be a valid URL")
    @Size(max = 255, message = "Cover image URL must be at most 255 characters long")
    @Schema(description = "Updated cover image URL for the deck", example = "https://example.com/images/new-spanish-flag.jpg", maxLength = 255)
    private String coverImageUrl;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Color must be a valid hex color code")
    @Size(min = 4, max = 7, message = "Color must be between 4 and 7 characters long")
    @Schema(description = "Updated theme color for the deck in hex format", example = "#3366FF", minLength = 4, maxLength = 7)
    private String color;

}
