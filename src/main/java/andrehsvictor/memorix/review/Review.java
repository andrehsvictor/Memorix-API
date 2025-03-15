package andrehsvictor.memorix.review;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "reviews")
@EqualsAndHashCode(of = { "id" })
@ToString(exclude = { "user", "card" })
public class Review implements Serializable {

    private static final long serialVersionUID = -7848260923496685040L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    private Integer rating;
    private Integer timeToAnswer;
    private boolean correct;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

}
