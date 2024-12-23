package andrehsvictor.memorix.deck.dto;

import java.util.Collection;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteDecksDto {

    @NotNull(message = "IDs must be provided")
    @NotEmpty(message = "IDs must be provided")
    private Collection<UUID> ids;

}
