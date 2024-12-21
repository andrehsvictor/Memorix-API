package andrehsvictor.memorix.deck;

import java.net.URI;
import java.util.Set;

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
import andrehsvictor.memorix.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DeckResource {

    private final DeckService deckService;
    private final DeckMapper deckMapper;

    @GetMapping("/v1/decks")
    public Page<GetDeckDto> getAll(Pageable pageable, @AuthenticationPrincipal User user) {
        Page<Deck> decks = deckService.getAllByUserId(user.getId(), pageable);
        return decks.map(deckMapper::deckToGetDeckDto);
    }

    @PostMapping("/v1/decks")
    public ResponseEntity<GetDeckDto> create(@RequestBody @Valid PostDeckDto postDeckDto,
            @AuthenticationPrincipal User user) {
        Deck deck = deckService.create(postDeckDto, user);
        URI location = URI.create("/v1/decks/" + deck.getSlug());
        return ResponseEntity.created(location).body(deckMapper.deckToGetDeckDto(deck));
    }

    @GetMapping("/v1/decks/{slug}")
    public GetDeckDto getBySlug(@PathVariable String slug, @AuthenticationPrincipal User user) {
        Deck deck = deckService.getBySlugAndUserId(slug, user.getId());
        return deckMapper.deckToGetDeckDto(deck);
    }

    @PutMapping("/v1/decks/{slug}")
    public GetDeckDto updateBySlug(@PathVariable String slug, @RequestBody @Valid PutDeckDto putDeckDto,
            @AuthenticationPrincipal User user) {
        Deck deck = deckService.update(slug, user.getId(), putDeckDto);
        return deckMapper.deckToGetDeckDto(deck);
    }

    @DeleteMapping("/v1/decks/{slug}")
    public ResponseEntity<Void> deleteBySlug(@PathVariable String slug, @AuthenticationPrincipal User user) {
        deckService.deleteBySlugAndUserId(slug, user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/v1/decks")
    public ResponseEntity<Void> deleteAllBySlugs(
            @RequestBody @Valid @NotEmpty(message = "Slugs must not be empty") Set<String> slugs,
            @AuthenticationPrincipal User user) {
        deckService.deleteAllBySlugsAndUserId(slugs, user.getId());
        return ResponseEntity.noContent().build();
    }
}
