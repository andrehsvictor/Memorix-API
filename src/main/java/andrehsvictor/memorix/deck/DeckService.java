package andrehsvictor.memorix.deck;

import java.util.Set;
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

@Service
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;
    private final DeckMapper deckMapper;
    private final Slugify slugify;

    public Deck create(PostDeckDto postDeckDto, User user) {
        String slug = slugify.slugify(postDeckDto.getName());
        if (existsBySlugAndUserId(slug, user.getId())) {
            throw new ResourceAlreadyExistsException("Deck with name '" + postDeckDto.getName() + "' already exists");
        }
        Deck deck = deckMapper.postDeckDtoToDeck(postDeckDto);
        deck.setSlug(slug);
        deck.setUser(user);
        return deckRepository.save(deck);
    }

    public Deck updateBySlug(String slug, UUID userId, PutDeckDto putDeckDto) {
        Deck deck = getBySlugAndUserId(slug, userId);
        String newSlug = slugify.slugify(putDeckDto.getName());
        if (!slug.equals(newSlug) && existsBySlugAndUserId(newSlug, userId)) {
            throw new ResourceAlreadyExistsException("Deck with name '" + putDeckDto.getName() + "' already exists");
        }
        deckMapper.updateDeckFromPutDeckDto(putDeckDto, deck);
        return deckRepository.save(deck);
    }

    public void deleteBySlug(String slug, UUID userId) {
        if (!existsBySlugAndUserId(slug, userId)) {
            throw new ResourceNotFoundException("Deck not found with slug '" + slug + "'");
        }
        deckRepository.deleteBySlugAndUserId(slug, userId);
    }

    public boolean existsBySlugAndUserId(String slug, UUID userId) {
        return deckRepository.existsBySlugAndUserId(slug, userId);
    }

    public Deck getBySlugAndUserId(String slug, UUID userId) {
        return deckRepository.findBySlugAndUserId(slug, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck not found with slug '" + slug + "'"));
    }

    public Page<Deck> getAllByUserId(UUID userId, Pageable pageable) {
        return deckRepository.findAllByUserId(userId, pageable);
    }

    public void deleteAllBySlugsAndUserId(Set<String> slugs, UUID userId) {
        deckRepository.deleteAllBySlugInAndUserId(slugs, userId);
    }
}
