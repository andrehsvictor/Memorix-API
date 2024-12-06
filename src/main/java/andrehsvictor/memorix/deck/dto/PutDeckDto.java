package andrehsvictor.memorix.deck.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutDeckDto {

    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @Size(max = 255, message = "Description must be between 3 and 255 characters")
    private String description;

    @Pattern(regexp = "^(PUBLIC|PRIVATE)$", message = "Visibility must be PUBLIC or PRIVATE")
    private String visibility;

    @Size(max = 255, message = "Cover URL must have a maximum of 255 characters")
    private String coverUrl;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Accent color must be a valid hex color")
    private String accentColor;

}
