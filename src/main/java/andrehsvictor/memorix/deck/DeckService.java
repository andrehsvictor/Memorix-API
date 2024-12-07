package andrehsvictor.memorix.deck;

import java.util.UUID;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.deck.dto.PostDeckDto;
import andrehsvictor.memorix.exception.ResourceAlreadyExistsException;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;
    private final DeckMapper deckMapper;

    public Deck create(PostDeckDto postDeckDto, User user) {
        Deck deck = deckMapper.postDeckDtoToDeck(postDeckDto);
        if (deckRepository.existsBySlugAndUserId(deck.getSlug(), user.getId())) {
            throw new ResourceAlreadyExistsException("Deck with slug '" + deck.getSlug() + "' already exists");
        }
        deck.setUser(user);
        deck = deckRepository.save(deck);
        return deck;
    }

    public Deck getBySlugAndUserId(String slug, UUID userId) {
        return deckRepository.findBySlugAndUserId(slug, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck with slug '" + slug + "' not found"));
    }

    public void deleteBySlug(String slug, UUID userId) {
        if (!deckRepository.existsBySlugAndUserId(slug, userId)) {
            throw new ResourceNotFoundException("Deck with slug '" + slug + "' not found");
        }
        deckRepository.deleteBySlug(slug);
    }

    public String generateSlug(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", "-");
    }

    public String generateAccentColor() {
        return "#" + Integer.toHexString((int) (Math.random() * 0xffffff));
    }
}
