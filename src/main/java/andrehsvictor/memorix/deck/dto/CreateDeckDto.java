package andrehsvictor.memorix.deck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Data Transfer Object for creating a new deck")
public class CreateDeckDto {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be at most 255 characters long")
    @Schema(description = "The name of the deck", example = "Spanish Vocabulary", maxLength = 255)
    private String name;

    @Size(max = 1000, message = "Description must be at most 1000 characters long")
    @Schema(description = "Optional description of the deck", example = "Basic Spanish vocabulary for beginners", maxLength = 1000)
    private String description;

    @Pattern(regexp = "^(https?://)?([a-zA-Z0-9.-]+)(:[0-9]+)?(/.*)?$", message = "Cover image URL must be a valid URL")
    @Size(max = 255, message = "Cover image URL must be at most 255 characters long")
    @Schema(description = "Optional cover image URL for the deck", example = "https://example.com/images/spanish-flag.jpg", maxLength = 255)
    private String coverImageUrl;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Color must be a valid hex color code")
    @Size(min = 4, max = 7, message = "Color must be between 4 and 7 characters long")
    @Schema(description = "Optional theme color for the deck in hex format", example = "#FF5733", minLength = 4, maxLength = 7)
    private String color;

}
