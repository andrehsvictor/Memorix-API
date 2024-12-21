package andrehsvictor.memorix.progress;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import andrehsvictor.memorix.card.Card;
import andrehsvictor.memorix.review.Review;
import andrehsvictor.memorix.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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

    @OneToOne
    @JoinColumn(name = "card_id")
    private Card card;

    @OneToMany(mappedBy = "progress")
    private Set<Review> reviews = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private ProgressStatus status = ProgressStatus.NEW;

    private Float easeFactor = 2.5f;
    private Integer repetitions = 0;
    private Integer interval = 1;
    private LocalDateTime nextRepetition = LocalDateTime.now();
    private LocalDateTime lastStudied;
    private Integer reviewsCount = 0;
    private Integer hits = 0;
    private Integer misses = 0;
    private Float averageTimeToAnswer = 0f;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void review(Integer rating, Integer timeToAnswer) {
        Review review = new Review();
        review.setRating(rating);
        review.setProgress(this);
        review.setTimeToAnswer(timeToAnswer);
        review.setHit(rating >= 3);
        this.reviews.add(review);
        this.reviewsCount++;

        this.easeFactor = this.easeFactor + (0.1f - (5 - rating) * (0.08f + (5 - rating) * 0.02f));
        if (this.easeFactor < 1.3f) {
            this.easeFactor = 1.3f;
        }

        if (rating < 3) {
            this.repetitions = 0;
            this.interval = 1;
            this.misses++;
            this.status = ProgressStatus.FORGOTTEN;
        } else {
            this.repetitions++;
            if (this.repetitions == 1) {
                this.interval = 1;
                this.status = ProgressStatus.LEARNING;
            } else if (this.repetitions == 2) {
                this.interval = 6;
            } else {
                this.interval = Math.round(this.interval * this.easeFactor);
                this.status = ProgressStatus.REVIEWING;
            }
            this.hits++;
        }

        this.averageTimeToAnswer = (this.averageTimeToAnswer * (this.hits + this.misses - 1) + timeToAnswer)
                / (this.hits + this.misses);
        this.lastStudied = LocalDateTime.now();
        this.nextRepetition = LocalDateTime.of(this.lastStudied.toLocalDate(), LocalTime.of(0, 0))
                .plusDays(this.interval);
    }

}
