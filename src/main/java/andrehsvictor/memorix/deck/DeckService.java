package andrehsvictor.memorix.deck;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;

    public Deck save(Deck deck) {
        return deckRepository.save(deck);
    }

    public Deck findById(UUID id) {
        return deckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deck not found with ID '" + id + "'"));
    }

    public void deleteById(UUID id) {
        deckRepository.deleteById(id);
    }

    public Page<Deck> findAll(Pageable pageable) {
        return deckRepository.findAll(pageable);
    }

    public Page<Deck> findAllByUserId(UUID userId, Pageable pageable) {
        return deckRepository.findAllByUsersUserId(userId, pageable);
    }

    public Deck findByIdAndUserIdOrVisibilityPublic(UUID id, UUID userId) {
        return deckRepository.findByIdAndUsersUserIdOrVisibility(id, userId, DeckVisibility.PUBLIC)
                .orElseThrow(() -> new ResourceNotFoundException("Deck not found with ID '" + id + "'"));
    }

    public Page<Deck> findAllByVisibilityPublic(Pageable pageable) {
        return deckRepository.findAllByVisibility(DeckVisibility.PUBLIC, pageable);
    }

    public Deck findByIdAndVisibilityPublic(UUID id) {
        return deckRepository.findByIdAndVisibility(id, DeckVisibility.PUBLIC)
                .orElseThrow(() -> new ResourceNotFoundException("Deck not found with ID '" + id + "'"));
    }

    public Page<Deck> findAllByOwnerUsernameAndVisibilityPublic(String username, Pageable pageable) {
        return deckRepository.findAllByOwnerUsernameAndVisibility(username, DeckVisibility.PUBLIC, pageable);
    }

    public String generateRandomAccentColor() {
        return "#" + Integer.toHexString((int) (Math.random() * 0xffffff)).toUpperCase();
    }
}
