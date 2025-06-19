package andrehsvictor.memorix.card;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Document(collection = "cards")
public class Card implements Serializable {

    private static final long serialVersionUID = 3333994900161536206L;

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String front;
    private String back;

    @Indexed
    private UUID deckId;

    @Indexed
    private UUID userId;

    @Builder.Default
    private Double easeFactor = 2.5;

    @Builder.Default
    private Integer interval = 0;

    @Builder.Default
    private Integer repetition = 0;

    @Builder.Default
    private LocalDateTime due = LocalDateTime.now();

    @Builder.Default
    private Integer reviewCount = 0;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

}
