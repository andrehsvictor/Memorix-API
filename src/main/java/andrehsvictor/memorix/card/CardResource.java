package andrehsvictor.memorix.card;

import java.net.URI;
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

import andrehsvictor.memorix.card.dto.GetCardDto;
import andrehsvictor.memorix.card.dto.PostCardDto;
import andrehsvictor.memorix.card.dto.PutCardDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CardResource {

    private final CardService cardService;
    private final CardMapper cardMapper;

    @GetMapping("/v1/cards")
    public Page<GetCardDto> getAll(Pageable pageable, @AuthenticationPrincipal UUID userId) {
        return cardService.getAllByUserId(userId, pageable).map(cardMapper::cardToGetCardDto);
    }

    @GetMapping("/v1/decks/{deckId}/cards")
    public Page<GetCardDto> getAllByDeckId(@PathVariable UUID deckId, Pageable pageable,
            @AuthenticationPrincipal UUID userId) {
        return cardService.getAllByUserIdAndDeckId(userId, deckId, pageable)
                .map(cardMapper::cardToGetCardDto);
    }

    @GetMapping("/v1/cards/{id}")
    public GetCardDto getById(@PathVariable UUID id, @AuthenticationPrincipal UUID userId) {
        return cardMapper.cardToGetCardDto(cardService.getByIdAndUserId(id, userId));
    }

    @PostMapping("/v1/decks/{deckId}/cards")
    public ResponseEntity<GetCardDto> create(@PathVariable UUID deckId,
            @RequestBody @Valid PostCardDto postCardDto,
            @AuthenticationPrincipal UUID userId) {
        GetCardDto getCardDto = cardMapper.cardToGetCardDto(
                cardService.create(postCardDto, deckId, userId));
        URI location = URI.create("/v1/cards/" + getCardDto.getId());
        return ResponseEntity.created(location).body(getCardDto);
    }

    @PutMapping("/v1/cards/{id}")
    public GetCardDto update(@PathVariable UUID id,
            @RequestBody @Valid PutCardDto putCardDto,
            @AuthenticationPrincipal UUID userId) {
        return cardMapper.cardToGetCardDto(cardService.update(id, putCardDto, userId));
    }

    @DeleteMapping("/v1/cards/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal UUID userId) {
        cardService.deleteByIdAndUserId(id, userId);
        return ResponseEntity.noContent().build();
    }

}
