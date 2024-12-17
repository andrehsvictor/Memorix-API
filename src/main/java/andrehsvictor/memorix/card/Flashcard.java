package andrehsvictor.memorix.card;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue("FLASHCARD")
public class Flashcard extends Card {

    private static final long serialVersionUID = 9195949790551628261L;

    private String answer;

}
