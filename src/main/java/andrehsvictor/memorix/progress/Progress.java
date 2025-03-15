package andrehsvictor.memorix.progress;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import andrehsvictor.memorix.card.Card;
import andrehsvictor.memorix.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@Table(name = "progresses")
@EqualsAndHashCode(of = { "id" })
@ToString(exclude = { "user", "card" })
public class Progress implements Serializable {

    private static final long serialVersionUID = -5158437978177590503L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    private LocalDateTime lastReviewedAt;
    private LocalDateTime nextReviewAt;
    private Float easinessFactor = 2.5f;
    private Integer consecutiveCorrectAnswers = 0;
    private Integer repetitions = 0;
    private Integer interval = 1;
    private Float meanRating = 0f;
    private Float meanTimeToAnswer = 0f;
    private Integer totalAnswers = 0;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();

}
