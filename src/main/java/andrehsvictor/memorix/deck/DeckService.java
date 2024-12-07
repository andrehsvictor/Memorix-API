package andrehsvictor.memorix.deck;

import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.slugify.Slugify;

import andrehsvictor.memorix.deck.dto.PostDeckDto;
import andrehsvictor.memorix.deck.dto.PutDeckDto;
import andrehsvictor.memorix.exception.ResourceAlreadyExistsException;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;
    private final Slugify slugify;
    private final DeckMapper deckMapper;

    public Deck create(PostDeckDto postDeckDto, User user) {
        Deck deck = deckMapper.postDeckDtoToDeck(postDeckDto);
        deck.setSlug(slugify.slugify(deck.getName()));
        deck.setUser(user);
        if (existsBySlugAndUserId(deck.getSlug(), user.getId())) {
            throw new ResourceAlreadyExistsException("Deck with slug '" + deck.getSlug() + "' already exists");
        }
        return deckRepository.save(deck);
    }

    public boolean existsBySlugAndUserId(String slug, UUID userId) {
        return deckRepository.existsBySlugAndUserId(slug, userId);
    }

    public Page<Deck> getAllByUserId(UUID userId, Pageable pageable) {
        return deckRepository.findAllByUserId(userId, pageable);
    }

    public Deck getBySlugAndUserId(String slug, UUID userId) {
        return deckRepository.findBySlugAndUserId(slug, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck with slug '" + slug + "' not found"));
    }

    public void deleteBySlug(String slug, UUID userId) {
        if (!existsBySlugAndUserId(slug, userId)) {
            throw new ResourceNotFoundException("Deck with slug '" + slug + "' not found");
        }
        deckRepository.deleteBySlug(slug);
    }

    public Deck updateBySlugAndUserId(String slug, UUID userId, PutDeckDto putDeckDto) {
        Deck deck = getBySlugAndUserId(slug, userId);
        deck = deckMapper.updateDeckFromPutDeckDto(putDeckDto, deck);
        deck.setSlug(slugify.slugify(deck.getName()));
        if (existsBySlugAndUserId(deck.getSlug(), userId)) {
            throw new ResourceAlreadyExistsException("Deck with slug '" + deck.getSlug() + "' already exists");
        }
        return deckRepository.save(deck);
    }

    public String generateSlug(String name) {
        try {
            String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
            String slug = normalized.replaceAll("[^\\p{ASCII}]", "")
                    .toLowerCase()
                    .trim()
                    .replaceAll("\\s+", "-");
            return URLEncoder.encode(slug, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Error generating slug");
        }
    }

    public String generateAccentColor() {
        return "#" + Integer.toHexString((int) (Math.random() * 0xffffff));
    }
}
