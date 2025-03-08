package andrehsvictor.memorix.deck;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import andrehsvictor.memorix.deckuser.AccessLevel;
import andrehsvictor.memorix.deckuser.DeckUser;
import andrehsvictor.memorix.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
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
@EqualsAndHashCode(of = { "id" })
@ToString(exclude = { "author", "usersWithAccess" })
public class Deck implements Serializable {

    private static final long serialVersionUID = 1824506724294082916L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    private String title;

    @Enumerated(EnumType.STRING)
    private DeckVisibility visibility = DeckVisibility.PRIVATE;

    private String coverUrl;

    private String accentColor;

    private Integer likesCount = 0;

    private Integer cardsCount = 0;

    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "deck")
    private Set<DeckUser> usersWithAccess = new HashSet<>();

    @PrePersist
    public void prePersist() {
        DeckUser deckUser = new DeckUser();
        deckUser.setDeck(this);
        deckUser.setUser(author);
        deckUser.setAccessLevel(AccessLevel.OWNER);
        usersWithAccess.add(deckUser);
    }
}