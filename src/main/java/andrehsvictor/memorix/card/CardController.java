package andrehsvictor.memorix.card;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.card.dto.CardDto;
import andrehsvictor.memorix.card.dto.CardStatsDto;
import andrehsvictor.memorix.card.dto.CreateCardDto;
import andrehsvictor.memorix.card.dto.UpdateCardDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping("/api/v1/cards")
    public Page<CardDto> getAll(
            Boolean due,
            Pageable pageable) {
        Page<Card> cards = cardService.getAll(due, pageable);
        return cards.map(cardService::toDto);
    }

    @GetMapping("/api/v1/cards/stats")
    public CardStatsDto getStats() {
        return cardService.getStats();
    }

    @GetMapping("/api/v1/decks/{deckId}/cards/stats")
    public CardStatsDto getStatsByDeckId(@PathVariable UUID deckId) {
        return cardService.getStatsByDeckId(deckId);
    }

    @GetMapping("/api/v1/decks/{deckId}/cards")
    public Page<CardDto> getAllByDeckId(
            @PathVariable UUID deckId,
            Boolean due,
            Pageable pageable) {
        Page<Card> cards = cardService.getAllByDeckId(deckId, due, pageable);
        return cards.map(cardService::toDto);
    }

    @GetMapping("/api/v1/cards/{cardId}")
    public CardDto getById(@PathVariable UUID cardId) {
        Card card = cardService.getById(cardId);
        return cardService.toDto(card);
    }

    @PostMapping("/api/v1/decks/{deckId}/cards")
    public CardDto create(
            @PathVariable UUID deckId,
            @Valid @RequestBody CreateCardDto createCardDto) {
        Card card = cardService.create(deckId, createCardDto);
        return cardService.toDto(card);
    }

    @PutMapping("/api/v1/cards/{cardId}")
    public CardDto update(
            @PathVariable UUID cardId,
            @Valid @RequestBody UpdateCardDto updateCardDto) {
        Card card = cardService.update(cardId, updateCardDto);
        return cardService.toDto(card);
    }

    @DeleteMapping("/api/v1/cards/{cardId}")
    public void delete(@PathVariable UUID cardId) {
        cardService.delete(cardId);
    }

}
