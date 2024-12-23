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
        URI location = URI.create("/v1/decks/" + deck.getId());
        return ResponseEntity.created(location).body(deckMapper.deckToGetDeckDto(deck));
    }

    @GetMapping("/v1/decks/{id}")
    public GetDeckDto getById(@PathVariable UUID id, @AuthenticationPrincipal UUID userId) {
        Deck deck = deckService.getByIdAndUserId(id, userId);
        return deckMapper.deckToGetDeckDto(deck);
    }

    @PutMapping("/v1/decks/{id}")
    public GetDeckDto updateById(@PathVariable UUID id,
            @RequestBody @Valid PutDeckDto putDeckDto,
            @AuthenticationPrincipal UUID userId) {
        Deck deck = deckService.update(id, putDeckDto, userId);
        return deckMapper.deckToGetDeckDto(deck);
    }

    @DeleteMapping("/v1/decks/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id, @AuthenticationPrincipal UUID userId) {
        deckService.deleteByIdAndUserId(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/v1/decks")
    public ResponseEntity<Void> deleteAllByIds(
            @RequestBody @Valid @NotEmpty(message = "At least one deck ID must be provided") Set<UUID> ids,
            @AuthenticationPrincipal UUID userId) {
        deckService.deleteAllByIdsAndUserId(ids, userId);
        return ResponseEntity.noContent().build();
    }
}
