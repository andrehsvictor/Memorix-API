package andrehsvictor.memorix.review;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import andrehsvictor.memorix.progress.Progress;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "reviews")
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "progress" })
public class Review implements Serializable {

    private static final long serialVersionUID = -7848260923496685040L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "progress_id")
    private Progress progress;

    private Integer timeToAnswer;
    private Boolean hit;
    private LocalDateTime createdAt = LocalDateTime.now();

}
