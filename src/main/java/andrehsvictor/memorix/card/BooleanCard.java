package andrehsvictor.memorix.card;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue("BOOLEAN")
public class BooleanCard extends Card {

    private static final long serialVersionUID = 7326703072899118237L;

    private boolean booleanAnswer;

}
