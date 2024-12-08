package andrehsvictor.memorix.deck;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.deck.dto.GetDeckDto;
import andrehsvictor.memorix.deck.dto.PostDeckDto;
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
        URI location = URI.create("/v1/decks/" + deck.getSlug());
        return ResponseEntity.created(location).body(deckMapper.deckToGetDeckDto(deck));
    }

    @GetMapping("/v1/decks/{slug}")
    public GetDeckDto getBySlug(@PathVariable String slug, @AuthenticationPrincipal User user) {
        Deck deck = deckService.getBySlugAndUserId(slug, user.getId());
        return deckMapper.deckToGetDeckDto(deck);
    }
}
