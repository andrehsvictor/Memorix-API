package andrehsvictor.memorix.deck.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDeckDto {

    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    @NotBlank(message = "Name is required")
    private String name;

    @Size(min = 3, max = 255, message = "Description must be between 3 and 255 characters")
    private String description;

    @Pattern(message = "Cover URL must be a valid URL", regexp = "^(http|https)://.*$")
    @Size(min = 11, max = 255, message = "Cover URL must be between 11 and 255 characters")
    private String coverUrl;

    @Size(min = 7, max = 7, message = "Accent color must be a valid HEX color")
    @Pattern(message = "Accent color must be a valid HEX color", regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")
    private String accentColor;

}
