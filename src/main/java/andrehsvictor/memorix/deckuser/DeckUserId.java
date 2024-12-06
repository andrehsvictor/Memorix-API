package andrehsvictor.memorix.deckuser;

import java.io.Serializable;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = { "deckId", "userId" })
public class DeckUserId implements Serializable {

    private static final long serialVersionUID = -1037118812047171284L;

    private UUID deckId;
    private UUID userId;

}
