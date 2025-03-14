package andrehsvictor.memorix.deckuser;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.deck.Deck;
import andrehsvictor.memorix.deck.DeckService;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeckUserService {

    private final DeckUserRepository deckUserRepository;
    private final UserService userService;
    private final DeckService deckService;

    public boolean hasAccessLevel(Long userId, Long deckId, AccessLevel accessLevel) {
        return deckUserRepository.existsByUserIdAndDeckIdAndAccessLevel(userId, deckId, accessLevel);
    }

    public boolean hasAccess(Long userId, Long deckId) {
        return deckUserRepository.existsByUserIdAndDeckId(userId, deckId);
    }

    public DeckUser create(Long userId, Long deckId, AccessLevel accessLevel) {
        DeckUser deckUser = new DeckUser();
        User user = userService.findById(userId);
        Deck deck = deckService.findById(deckId);
        deckUser.setUser(user);
        deckUser.setDeck(deck);
        deckUser.setAccessLevel(accessLevel);
        return deckUserRepository.save(deckUser);
    }

    public void delete(Long userId, Long deckId) {
        deckUserRepository.deleteByUserIdAndDeckId(userId, deckId);
    }

}
