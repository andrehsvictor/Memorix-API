package andrehsvictor.memorix.deck;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.deck.dto.GetDeckDto;
import andrehsvictor.memorix.deck.dto.PostDeckDto;
import andrehsvictor.memorix.deck.dto.PutDeckDto;
import andrehsvictor.memorix.deckuser.DeckUser;
import andrehsvictor.memorix.deckuser.DeckUserRole;
import andrehsvictor.memorix.deckuser.DeckUserService;
import andrehsvictor.memorix.exception.ForbiddenActionException;
import andrehsvictor.memorix.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeckFacade {

    private final DeckService deckService;
    private final DeckUserService deckUserService;
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

    public GetDeckDto create(PostDeckDto postDeckDto, User user) {
        Deck deck = deckMapper.postDeckDtoToDeck(postDeckDto);
        deck.setOwner(user);
        deck = deckService.save(deck);
        DeckUser deckUser = of(user, deck, DeckUserRole.OWNER);
        deckUserService.save(deckUser);
        return deckMapper.deckToGetDeckDto(deck);
    }

    private DeckUser of(User user, Deck deck, DeckUserRole role) {
        return DeckUser.builder()
                .deck(deck)
                .user(user)
                .role(role)
                .build();
    }

    public void deleteById(UUID id, User user) {
        Deck deck = deckService.findByIdAndUserIdOrVisibilityPublic(id, user.getId());
        if (!deck.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenActionException("You are not allowed to delete this deck");
        }
        deckService.deleteById(id);
    }

    public GetDeckDto update(UUID id, PutDeckDto putDeckDto, User user) {
        Deck deck = deckService.findByIdAndUserIdOrVisibilityPublic(id, user.getId());
        if (!deck.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenActionException("You are not allowed to update this deck");
        }
        deck = deckMapper.updateDeckFromPutDeckDto(putDeckDto, deck);
        deck = deckService.save(deck);
        return deckMapper.deckToGetDeckDto(deck);
    }

    public void add(UUID id, User user) {
        Deck deck = deckService.findByIdAndUserIdOrVisibilityPublic(id, user.getId());
        if (deckUserService.existsByDeckIdAndUserId(deck.getId(), user.getId())) {
            throw new ForbiddenActionException("You already have access to this deck");
        }
        DeckUser deckUser = of(user, deck, DeckUserRole.USER);
        deckUserService.save(deckUser);
    }

    public void remove(UUID id, User user) {
        Deck deck = deckService.findByIdAndUserIdOrVisibilityPublic(id, user.getId());
        if (!deckUserService.existsByDeckIdAndUserId(deck.getId(), user.getId())) {
            throw new ForbiddenActionException("You do not have access to this deck");
        }
        deckUserService.deleteByDeckIdAndUserId(deck.getId(), user.getId());
    }

}
