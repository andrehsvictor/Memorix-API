package andrehsvictor.memorix.deck;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.deck.dto.GetDeckDto;
import andrehsvictor.memorix.deck.dto.PostDeckDto;
import andrehsvictor.memorix.deck.dto.PutDeckDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DeckResource {

    private final DeckService deckService;
    private final DeckMapper deckMapper;

    @GetMapping("/v1/decks")
    public Page<GetDeckDto> getAll(Pageable pageable, @AuthenticationPrincipal UUID userId) {
        Page<Deck> decks = deckService.getAllByUserId(userId, pageable);
        return decks.map(deckMapper::deckToGetDeckDto);
    }

    @PostMapping("/v1/decks")
    public ResponseEntity<GetDeckDto> create(@RequestBody @Valid PostDeckDto postDeckDto,
            @AuthenticationPrincipal UUID userId) {
        Deck deck = deckService.create(postDeckDto, userId);
        URI location = URI.create("/v1/decks/" + deck.getSlug());
        return ResponseEntity.created(location).body(deckMapper.deckToGetDeckDto(deck));
    }

    @GetMapping("/v1/decks/{slug}")
    public GetDeckDto getBySlug(@PathVariable String slug, @AuthenticationPrincipal UUID userId) {
        Deck deck = deckService.getBySlugAndUserId(slug, userId);
        return deckMapper.deckToGetDeckDto(deck);
    }

    @PutMapping("/v1/decks/{slug}")
    public GetDeckDto updateBySlug(@PathVariable String slug,
            @RequestBody @Valid PutDeckDto putDeckDto,
            @AuthenticationPrincipal UUID userId) {
        Deck deck = deckService.update(slug, userId, putDeckDto);
        return deckMapper.deckToGetDeckDto(deck);
    }

    @DeleteMapping("/v1/decks/{slug}")
    public ResponseEntity<Void> deleteBySlug(@PathVariable String slug, @AuthenticationPrincipal UUID userId) {
        deckService.deleteBySlugAndUserId(slug, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/v1/decks")
    public ResponseEntity<Void> deleteAllBySlugs(
            @RequestBody @Valid @NotEmpty(message = "Slugs must not be empty") Set<String> slugs,
            @AuthenticationPrincipal UUID userId) {
        deckService.deleteAllBySlugsAndUserId(slugs, userId);
        return ResponseEntity.noContent().build();
    }
}
