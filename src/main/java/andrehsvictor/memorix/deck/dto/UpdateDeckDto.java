package andrehsvictor.memorix.deck.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateDeckDto {

    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @Pattern(regexp = "^(PUBLIC|PRIVATE)$", message = "Invalid visibility")
    private String visibility;

    @Pattern(message = "Invalid URL", regexp = "^(http|https)://.*$")
    private String coverUrl;

    @Pattern(message = "Accent color must be a valid HEX color code", regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")
    private String accentColor;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;
}
