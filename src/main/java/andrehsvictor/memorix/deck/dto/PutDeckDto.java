package andrehsvictor.memorix.deck.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutDeckDto {

    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

    @Size(min = 7, max = 7, message = "Accent color must be a valid HEX color")
    private String accentColor;

    @Size(max = 255, message = "Cover URL must be at most 255 characters")
    private String coverUrl;
    
}
