package andrehsvictor.memorix.card;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.card.dto.GetCardDto;
import andrehsvictor.memorix.card.dto.PostCardDto;
import andrehsvictor.memorix.card.dto.PutCardDto;
import andrehsvictor.memorix.card.dto.ReviewDto;
import andrehsvictor.memorix.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CardResource {

    private final CardService cardService;
    private final CardMapper cardMapper;

    @GetMapping("/v1/cards")
    public Page<GetCardDto> getAll(Pageable pageable, @AuthenticationPrincipal User user) {
        return cardService.getAllByDeckUserId(user.getId(), pageable).map(cardMapper::cardToGetCardDto);
    }

    @GetMapping("/v1/decks/{deckSlug}/cards")
    public Page<GetCardDto> getAllByDeckSlug(@PathVariable String deckSlug, Pageable pageable,
            @AuthenticationPrincipal User user) {
        return cardService.getAllByDeckUserIdAndDeckSlug(user.getId(), deckSlug, pageable)
                .map(cardMapper::cardToGetCardDto);
    }

    @GetMapping("/v1/cards/{id}")
    public GetCardDto getById(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        return cardMapper.cardToGetCardDto(cardService.getByIdAndDeckUserId(id, user.getId()));
    }

    @PostMapping("/v1/decks/{deckSlug}/cards")
    public ResponseEntity<GetCardDto> create(@PathVariable String deckSlug,
            @RequestBody @Valid PostCardDto postCardDto,
            @AuthenticationPrincipal User user) {
        GetCardDto getCardDto = cardMapper.cardToGetCardDto(
                cardService.create(postCardDto, deckSlug, user));
        URI location = URI.create("/v1/cards/" + getCardDto.getId());
        return ResponseEntity.created(location).body(getCardDto);
    }

    @PutMapping("/v1/cards/{id}")
    public GetCardDto update(@PathVariable UUID id,
            @RequestBody @Valid PutCardDto putCardDto,
            @AuthenticationPrincipal User user) {
        return cardMapper.cardToGetCardDto(cardService.update(id, putCardDto, user.getId()));
    }

    @DeleteMapping("/v1/cards/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        cardService.deleteByIdAndDeckUserId(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/v1/cards/{id}/review")
    public ResponseEntity<Void> review(@PathVariable UUID id,
            @RequestBody @Valid ReviewDto reviewDto,
            @AuthenticationPrincipal User user) {
        cardService.review(id, user.getId(), reviewDto);
        return ResponseEntity.noContent().build();
    }
}
