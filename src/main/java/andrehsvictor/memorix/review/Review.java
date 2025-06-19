package andrehsvictor.memorix.review;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
public class Review implements Serializable {

    private static final long serialVersionUID = -8836605389317238321L;

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Indexed
    private UUID cardId;

    @Indexed
    private UUID userId;

    private Integer rating;

    private Integer responseTime; // in milliseconds

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

}
