package andrehsvictor.memorix.card.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCardDto {

    @Size(max = 2000, message = "Front must be at most 2000 characters long")
    private String front;

    @Size(max = 2000, message = "Back must be at most 2000 characters long")
    private String back;

}
