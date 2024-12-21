package andrehsvictor.memorix.progress;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import andrehsvictor.memorix.card.Card;
import andrehsvictor.memorix.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "progress")
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "user", "card" })
public class Progress implements Serializable {

    private static final long serialVersionUID = -8269570084543045204L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    @Enumerated(EnumType.STRING)
    private ProgressStatus status = ProgressStatus.NEW;

    private Float easeFactor = 2.5f;
    private Integer repetitions = 0;
    private Integer interval = 1;
    private LocalDateTime nextRepetition = LocalDateTime.now();
    private LocalDateTime lastStudied;
    private Integer hits = 0;
    private Integer misses = 0;
    private Float averageTimeToAnswer = 0f;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
