package andrehsvictor.memorix.deck;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.deck.dto.GetDeckDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeckFacade {

    private final DeckService deckService;
    private final DeckMapper deckMapper;

    public GetDeckDto findByIdAndUserIdOrVisibilityPublic(UUID id, UUID userId) {
        Deck deck = deckService.findByIdAndUserIdOrVisibilityPublic(id, userId);
        return deckMapper.deckToGetDeckDto(deck);
    }

    public Page<GetDeckDto> findAllByUserId(UUID userId, Pageable pageable) {
        Page<Deck> decks = deckService.findAllByUserId(userId, pageable);
        return decks.map(deckMapper::deckToGetDeckDto);
    }

    public Page<GetDeckDto> findAllByVisibilityPublic(Pageable pageable) {
        Page<Deck> decks = deckService.findAllByVisibilityPublic(pageable);
        return decks.map(deckMapper::deckToGetDeckDto);
    }

    public GetDeckDto findByIdAndVisibilityPublic(UUID id) {
        Deck deck = deckService.findByIdAndVisibilityPublic(id);
        return deckMapper.deckToGetDeckDto(deck);
    }

    public Page<GetDeckDto> findAllByOwnerUsernameAndVisibilityPublic(String username, Pageable pageable) {
        Page<Deck> decks = deckService.findAllByOwnerUsernameAndVisibilityPublic(username, pageable);
        return decks.map(deckMapper::deckToGetDeckDto);
    }

}
