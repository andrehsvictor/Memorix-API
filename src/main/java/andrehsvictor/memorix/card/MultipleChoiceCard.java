package andrehsvictor.memorix.card;

import java.util.Set;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue("MULTIPLE_CHOICE")
public class MultipleChoiceCard extends Card {

    private static final long serialVersionUID = -7679772184524238187L;

    @ElementCollection
    private Set<String> options;

    private Integer correctOptionIndex;

}
