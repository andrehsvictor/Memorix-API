package andrehsvictor.memorix.deck.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDeckDto {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50)
    private String name;

    @Size(min = 3, max = 255)
    private String description;

    @NotBlank(message = "Visibility is required")
    @Pattern(regexp = "^(PUBLIC|PRIVATE)$", message = "Visibility must be PUBLIC or PRIVATE")
    private String visibility;

    @Size(min = 11, max = 255)
    @Pattern(message = "Cover URL must be a valid URL", regexp = "^(http|https)://.*$")
    private String coverUrl;
    
}
