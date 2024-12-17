package andrehsvictor.memorix.answer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import andrehsvictor.memorix.card.Card;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "cards")
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "card" })
public class Answer implements Serializable {

    private static final long serialVersionUID = -8077272231391356081L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    private String answer;
    private boolean correct;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
