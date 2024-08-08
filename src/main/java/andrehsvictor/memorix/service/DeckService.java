package andrehsvictor.memorix.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.entity.Deck;
import andrehsvictor.memorix.entity.User;
import andrehsvictor.memorix.exception.MemorixException;
import andrehsvictor.memorix.repository.DeckRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;
    private final UserService userService;

    private static final String DECK_NOT_FOUND_ID = "Deck not found with id: %d";
    private static final String DECK_NOT_FOUND_SLUG = "Deck not found with slug: %s";

    public Page<Deck> findAll(Pageable pageable) {
        User user = userService.findAuthenticatedUser();
        return deckRepository.findAll(user, pageable);
    }

    public Deck findBySlug(String slug) {
        User user = userService.findAuthenticatedUser();
        return deckRepository.findBySlug(slug, user).orElseThrow(
                () -> new MemorixException(HttpStatus.NOT_FOUND, String.format(DECK_NOT_FOUND_SLUG, slug)));
    }

    public Deck findById(Long id) {
        User user = userService.findAuthenticatedUser();
        return deckRepository.findById(id, user)
                .orElseThrow(() -> new MemorixException(HttpStatus.NOT_FOUND, String.format(DECK_NOT_FOUND_ID, id)));
    }

    public Deck save(Deck deck) {
        User user = userService.findAuthenticatedUser();
        deck.setUser(user);
        deck.setSlug(deck.getName().toLowerCase().replace(" ", "-"));
        return deckRepository.save(deck);
    }

    public void delete(String slug) {
        Deck deck = findBySlug(slug);
        deckRepository.delete(deck);
    }

    public void delete(Long id) {
        Deck deck = findById(id);
        deckRepository.delete(deck);
    }

    public Deck update(Long id, Deck deck) {
        Deck existingDeck = findById(id);
        updateDeckFields(deck, existingDeck);
        return deckRepository.save(existingDeck);
    }

    public Deck update(String slug, Deck deck) {
        Deck existingDeck = findBySlug(slug);
        updateDeckFields(deck, existingDeck);
        return deckRepository.save(existingDeck);
    }

    private void updateDeckFields(Deck deck, Deck existingDeck) {
        existingDeck.setName(deck.getName() != null ? deck.getName() : existingDeck.getName());
        existingDeck.setSlug(existingDeck.getName().toLowerCase().replace(" ", "-"));
        existingDeck.setDescription(deck.getDescription() != null ? deck.getDescription() : existingDeck.getDescription());
        existingDeck.setUpdatedAt(deck.getUpdatedAt());
    }
}
