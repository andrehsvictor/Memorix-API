package andrehsvictor.memorix.deck;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.deck.dto.GetDeckDto;
import andrehsvictor.memorix.user.User;
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
    public GetDeckDto findById(@PathVariable String id, JwtAuthenticationToken jwt,
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
}
