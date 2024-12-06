package andrehsvictor.memorix.deckuser;

import java.io.Serializable;
import java.time.LocalDateTime;

import andrehsvictor.memorix.deck.Deck;
import andrehsvictor.memorix.user.User;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
@Table(name = "decks_users")
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "user", "deck" })
public class DeckUser implements Serializable {

    private static final long serialVersionUID = -6448320900227736293L;

    @EmbeddedId
    private DeckUserId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("deckId")
    @JoinColumn(name = "deck_id")
    private Deck deck;

    @Enumerated(EnumType.STRING)
    private DeckUserRole role = DeckUserRole.USER;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
