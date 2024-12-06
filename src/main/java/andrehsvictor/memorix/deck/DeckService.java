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
}
