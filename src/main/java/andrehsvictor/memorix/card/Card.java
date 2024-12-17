package andrehsvictor.memorix.card;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import andrehsvictor.memorix.deck.Deck;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
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
@ToString(exclude = { "deck" })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Card implements Serializable {

    private static final long serialVersionUID = 1446446225070354526L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String question;

    @Enumerated(EnumType.STRING)
    private CardType type;

    @ManyToOne
    @JoinColumn(name = "deck_id")
    private Deck deck;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
