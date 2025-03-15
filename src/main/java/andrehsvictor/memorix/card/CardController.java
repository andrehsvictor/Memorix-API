package andrehsvictor.memorix.card;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.card.dto.CardDto;
import andrehsvictor.memorix.card.dto.CardFilterDto;
import andrehsvictor.memorix.card.dto.CreateCardDto;
import andrehsvictor.memorix.card.dto.UpdateCardDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/api/v1/cards")
    public ResponseEntity<Page<CardDto>> findAll(
            CardFilterDto cardFilterDto,
            Pageable pageable) {
        Page<CardDto> cards = cardService.findAll(
                cardFilterDto,
                pageable)
                .map(cardService::toDto);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/api/v1/decks/{deckId}/cards")
    public ResponseEntity<Page<CardDto>> findAllByDeckId(
            Long deckId,
            CardFilterDto cardFilterDto,
            Pageable pageable) {
        Page<CardDto> cards = cardService.findAllByDeckId(
                deckId,
                cardFilterDto,
                pageable)
                .map(cardService::toDto);
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/api/v1/decks/{deckId}/cards")
    public ResponseEntity<CardDto> create(
            @PathVariable Long deckId,
            @Valid @RequestBody CreateCardDto createCardDto) {
        Card card = cardService.create(deckId, createCardDto);
        URI location = URI.create("/api/v1/cards/" + card.getId());
        return ResponseEntity.created(location).body(cardService.toDto(card));
    }

    @GetMapping("/api/v1/cards/{id}")
    public ResponseEntity<CardDto> findById(@PathVariable Long id) {
        Card card = cardService.findById(id);
        return ResponseEntity.ok(cardService.toDto(card));
    }

    @PutMapping("/api/v1/cards/{id}")
    public ResponseEntity<CardDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCardDto updateCardDto) {
        Card card = cardService.update(id, updateCardDto);
        return ResponseEntity.ok(cardService.toDto(card));
    }

    @DeleteMapping("/api/v1/cards/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
