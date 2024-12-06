package andrehsvictor.memorix.deck;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.Formula;

import andrehsvictor.memorix.user.User;
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
@Table(name = "decks")
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "user" })
public class Deck implements Serializable {

    private static final long serialVersionUID = 8581858367896118781L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String slug;
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String coverUrl;
    private String accentColor;
    private Long cardsCount = 0L;

    @Formula("""
            (SELECT COUNT(*)
            FROM cards c
            JOIN progresses p ON c.id = p.card_id
            WHERE c.deck_id = id AND p.user_id = user_id AND p.next_repetition <= CURRENT_TIMESTAMP)
            """)
    private Long scheduledCardsCount = 0L;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
