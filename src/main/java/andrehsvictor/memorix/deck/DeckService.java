package andrehsvictor.memorix.deck;

import java.util.UUID;

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

    public Deck findBySlugAndUserId(String slug, UUID userId) {
        return deckRepository.findBySlugAndUserId(slug, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck not found with slug '" + slug + "'"));
    }

    public void deleteBySlug(String slug) {
        deckRepository.deleteBySlug(slug);
    }
}
