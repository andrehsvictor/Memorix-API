package andrehsvictor.memorix.deck;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final DeckFacade deckFacade;

    @GetMapping("/v1/decks")
    public Page<GetDeckDto> findAllPublic(Pageable pageable) {
        return deckFacade.findAllByVisibilityPublic(pageable);
    }

    @GetMapping("/v1/decks/{id}")
    public GetDeckDto findById(@PathVariable String id,
            @AuthenticationPrincipal User user) {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
            return deckFacade.findByIdAndVisibilityPublic(UUID.fromString(id));
        } else {
            return deckFacade.findByIdAndUserIdOrVisibilityPublic(UUID.fromString(id), user.getId());
        }
    }

    @GetMapping("/v1/users/{username}/decks")
    public Page<GetDeckDto> findAllByOwnerUsernameAndVisibilityPublic(@PathVariable String username,
            Pageable pageable) {
        return deckFacade.findAllByOwnerUsernameAndVisibilityPublic(username, pageable);
    }

    @GetMapping("/v1/users/me/decks")
    public Page<GetDeckDto> findAllMine(Pageable pageable, @AuthenticationPrincipal User user) {
        return deckFacade.findAllByUserId(user.getId(), pageable);
    }

    @PostMapping("/v1/decks")
    public ResponseEntity<GetDeckDto> create(@Valid @RequestBody PostDeckDto postDeckDto,
            @AuthenticationPrincipal User user) {
        GetDeckDto deckDto = deckFacade.create(postDeckDto, user);
        URI location = URI.create("/v1/decks/" + deckDto.getId());
        return ResponseEntity.created(location).body(deckDto);
    }

    @PutMapping("/v1/decks/{id}")
    public GetDeckDto update(@PathVariable String id, @Valid @RequestBody PutDeckDto putDeckDto,
            @AuthenticationPrincipal User user) {
        return deckFacade.update(UUID.fromString(id), putDeckDto, user);
    }

    @DeleteMapping("/v1/decks/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, @AuthenticationPrincipal User user) {
        deckFacade.deleteById(UUID.fromString(id), user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/v1/users/me/decks/{id}")
    public ResponseEntity<Void> add(@PathVariable String id, @AuthenticationPrincipal User user) {
        deckFacade.add(UUID.fromString(id), user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/v1/users/me/decks/{id}")
    public ResponseEntity<Void> remove(@PathVariable String id, @AuthenticationPrincipal User user) {
        deckFacade.remove(UUID.fromString(id), user);
        return ResponseEntity.noContent().build();
    }
}
