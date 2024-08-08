package andrehsvictor.memorix.dto.response;

import java.time.ZoneOffset;

import org.springframework.hateoas.RepresentationModel;

import andrehsvictor.memorix.entity.Deck;
import lombok.Data;

@Data
public class DeckResponseDTO extends RepresentationModel<DeckResponseDTO> {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Long createdAt;
    private Long updatedAt;
    private Long totalCards;
    private Long totalCardsToReview;

    public DeckResponseDTO(Deck deck) {
        this.id = deck.getId();
        this.name = deck.getName();
        this.slug = deck.getSlug();
        this.description = deck.getDescription();
        this.createdAt = deck.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        this.updatedAt = deck.getUpdatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        this.totalCards = 0L;
        this.totalCardsToReview = 0L;
    }
}
