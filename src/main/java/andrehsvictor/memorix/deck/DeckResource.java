package andrehsvictor.memorix.deck;

import java.net.URI;

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
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DeckResource {

    private final DeckService deckService;
    private final DeckMapper deckMapper;

    @PostMapping("/v1/decks")
    public ResponseEntity<GetDeckDto> create(@RequestBody @Valid PostDeckDto postDeckDto,
            @AuthenticationPrincipal User user) {
        Deck deck = deckService.create(postDeckDto, user);
        GetDeckDto getDeckDto = deckMapper.deckToGetDeckDto(deck);
        URI location = URI.create("/v1/decks/" + getDeckDto.getSlug());
        return ResponseEntity.created(location).body(getDeckDto);
    }

    @GetMapping("/v1/decks/{slug}")
    public GetDeckDto getBySlug(@AuthenticationPrincipal User user, @PathVariable String slug) {
        Deck deck = deckService.getBySlugAndUserId(slug, user.getId());
        return deckMapper.deckToGetDeckDto(deck);
    }

    @GetMapping("/v1/decks")
    public Page<GetDeckDto> getAll(@AuthenticationPrincipal User user, Pageable pageable) {
        Page<Deck> decks = deckService.getAllByUserId(user.getId(), pageable);
        return decks.map(deckMapper::deckToGetDeckDto);
    }

    @PutMapping("/v1/decks/{slug}")
    public GetDeckDto update(@AuthenticationPrincipal User user, @PathVariable String slug,
            @RequestBody @Valid PutDeckDto putDeckDto) {
        Deck deck = deckService.updateBySlugAndUserId(slug, user.getId(), putDeckDto);
        return deckMapper.deckToGetDeckDto(deck);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable String slug) {
        deckService.deleteBySlug(slug, user.getId());
        return ResponseEntity.noContent().build();
    }

}
