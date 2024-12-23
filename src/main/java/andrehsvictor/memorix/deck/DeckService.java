package andrehsvictor.memorix.deck;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.deck.dto.PostDeckDto;
import andrehsvictor.memorix.deck.dto.PutDeckDto;
import andrehsvictor.memorix.exception.ResourceAlreadyExistsException;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;
    private final DeckMapper deckMapper;
    private final UserService userService;

    public Deck create(PostDeckDto postDeckDto, UUID userId) {
        User user = userService.getById(userId);
        if (existsByNameAndUserId(postDeckDto.getName(), userId)) {
            throw new ResourceAlreadyExistsException("Deck with name '" + postDeckDto.getName() + "' already exists");
        }
        Deck deck = deckMapper.postDeckDtoToDeck(postDeckDto);
        deck.setUser(user);
        return deckRepository.save(deck);
    }

    public Deck update(UUID id, PutDeckDto putDeckDto, UUID userId) {
        Deck deck = getByIdAndUserId(id, userId);
        if (existsByNameAndUserId(putDeckDto.getName(), userId) && !deck.getName().equals(putDeckDto.getName())) {
            throw new ResourceAlreadyExistsException("Deck with name '" + putDeckDto.getName() + "' already exists");
        }
        deckMapper.updateDeckFromPutDeckDto(putDeckDto, deck);
        return deckRepository.save(deck);
    }

    public void deleteByIdAndUserId(UUID id, UUID userId) {
        if (!existsByIdAndUserId(id, userId)) {
            throw new ResourceNotFoundException("Deck not found with ID '" + id + "'");
        }
        deckRepository.deleteByIdAndUserId(id, userId);
    }

    public void incrementCardsCount(Deck deck) {
        deck.setCardsCount(deck.getCardsCount() + 1);
        deckRepository.save(deck);
    }

    public void decrementCardsCount(Deck deck) {
        deck.setCardsCount(deck.getCardsCount() - 1);
        deckRepository.save(deck);
    }

    public boolean existsByIdAndUserId(UUID id, UUID userId) {
        return deckRepository.existsByIdAndUserId(id, userId);
    }

    public boolean existsByNameAndUserId(String slug, UUID userId) {
        return deckRepository.existsByNameAndUserId(slug, userId);
    }

    public Deck getByIdAndUserId(UUID id, UUID userId) {
        return deckRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck not found with ID '" + id + "'"));
    }

    public Page<Deck> getAllByUserId(UUID userId, Pageable pageable) {
        return deckRepository.findAllByUserId(userId, pageable);
    }

    public void deleteAllByIdsAndUserId(Set<UUID> ids, UUID userId) {
        deckRepository.deleteAllByIdInAndUserId(ids, userId);
    }
}
