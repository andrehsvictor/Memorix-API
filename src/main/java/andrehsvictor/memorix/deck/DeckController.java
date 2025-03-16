package andrehsvictor.memorix.deck;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.deck.dto.CreateDeckDto;
import andrehsvictor.memorix.deck.dto.DeckDto;
import andrehsvictor.memorix.deck.dto.UpdateDeckDto;
import andrehsvictor.memorix.deck.dto.UpdateDeckVisibilityDto;
import andrehsvictor.memorix.deckuser.AccessLevel;
import andrehsvictor.memorix.util.EnumUtil;
import andrehsvictor.memorix.util.StringUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;

    @GetMapping("/api/v1/decks")
    public ResponseEntity<Page<DeckDto>> findAll(@RequestParam(required = false, name = "q") String query,
            Pageable pageable) {
        query = StringUtil.normalize(query);
        return ResponseEntity.ok(deckService.findAllPublic(query, pageable).map(deckService::toDto));
    }

    @GetMapping("/api/v1/users/me/decks")
    public ResponseEntity<Page<DeckDto>> findAllMine(
            @RequestParam(required = false, name = "q") String query,
            @Valid @RequestParam(required = false) @Pattern(regexp = "public|private") String visibility,
            @Valid @RequestParam(required = false) @Pattern(regexp = "owner|viewer|editor") String accessLevel,
            Pageable pageable) {
        DeckVisibility visibilityEnum = EnumUtil.convertStringToEnum(DeckVisibility.class, visibility);
        AccessLevel accessLevelEnum = EnumUtil.convertStringToEnum(AccessLevel.class, accessLevel);
        Page<Deck> decks = deckService.findAll(
                query,
                visibilityEnum,
                accessLevelEnum,
                pageable);
        return ResponseEntity.ok(decks.map(deckService::toDto));
    }

    @GetMapping("/api/v1/decks/{id}")
    public ResponseEntity<DeckDto> findById(@PathVariable Long id) {
        Deck deck = deckService.findById(id);
        return ResponseEntity.ok(deckService.toDto(deck));
    }

    @GetMapping("/api/v1/users/{id}/decks")
    public ResponseEntity<Page<DeckDto>> findAllByAuthorId(@PathVariable Long id,
            @RequestParam(required = false, name = "q") String query, Pageable pageable) {
        query = StringUtil.normalize(query);
        return ResponseEntity.ok(deckService.findAllByAuthorId(query, id, pageable).map(deckService::toDto));
    }

    @PostMapping("/api/v1/decks")
    public ResponseEntity<DeckDto> create(@Valid @RequestBody CreateDeckDto createDeckDto) {
        Deck deck = deckService.create(createDeckDto);
        URI location = URI.create("/api/v1/decks/" + deck.getId());
        return ResponseEntity.created(location).body(deckService.toDto(deck));
    }

    @PutMapping("/api/v1/decks/{id}")
    public ResponseEntity<DeckDto> update(@PathVariable Long id, @Valid @RequestBody UpdateDeckDto updateDeckDto) {
        Deck deck = deckService.update(id, updateDeckDto);
        return ResponseEntity.ok(deckService.toDto(deck));
    }

    @PatchMapping("/api/v1/decks/{id}/visibility")
    public ResponseEntity<Void> updateVisibility(@PathVariable Long id,
            @Valid @RequestBody UpdateDeckVisibilityDto updateDeckVisibilityDto) {
        deckService.updateVisibility(id, updateDeckVisibilityDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/v1/decks/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deckService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/v1/users/me/decks/{id}")
    public ResponseEntity<Void> appendToCurrentUser(@PathVariable Long id) {
        deckService.append(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/v1/users/me/decks/{id}")
    public ResponseEntity<Void> removeFromCurrentUser(@PathVariable Long id) {
        deckService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/v1/decks/{id}/likes")
    public ResponseEntity<Void> like(@PathVariable Long id) {
        deckService.like(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/v1/decks/{id}/likes")
    public ResponseEntity<Void> unlike(@PathVariable Long id) {
        deckService.unlike(id);
        return ResponseEntity.noContent().build();
    }

}
