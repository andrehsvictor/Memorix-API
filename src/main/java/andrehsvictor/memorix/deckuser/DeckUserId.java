package andrehsvictor.memorix.deckuser;

import java.io.Serializable;

import andrehsvictor.memorix.deck.Deck;
import andrehsvictor.memorix.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckUserId implements Serializable {

    private static final long serialVersionUID = -1037118812047171284L;
    
    private User user;
    private Deck deck;
}