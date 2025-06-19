package andrehsvictor.memorix.deck;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.deck.dto.CreateDeckDto;
import andrehsvictor.memorix.deck.dto.DeckDto;
import andrehsvictor.memorix.deck.dto.UpdateDeckDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;

    @GetMapping("/api/v1/decks")
    public Page<DeckDto> getAll(
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Boolean includeWithCoverImage,
            @RequestParam(required = false) Boolean includeEmpty,
            Pageable pageable) {
        return deckService.getAllWithFilters(
                query,
                name,
                description,
                includeWithCoverImage,
                includeEmpty,
                pageable)
                .map(deckService::toDto);
    }

    @GetMapping("/api/v1/decks/{id}")
    public DeckDto getById(@PathVariable UUID id) {
        return deckService.toDto(deckService.getById(id));
    }

    @PostMapping("/api/v1/decks")
    public DeckDto create(@Valid @RequestBody CreateDeckDto createDeckDto) {
        Deck deck = deckService.create(createDeckDto);
        return deckService.toDto(deck);
    }

    @PutMapping("/api/v1/decks/{id}")
    public DeckDto update(@PathVariable UUID id, @Valid @RequestBody UpdateDeckDto updateDeckDto) {
        Deck deck = deckService.update(id, updateDeckDto);
        return deckService.toDto(deck);
    }

    @DeleteMapping("/api/v1/decks/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deckService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
