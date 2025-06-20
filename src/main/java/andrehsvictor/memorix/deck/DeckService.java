package andrehsvictor.memorix.deck;

import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.common.exception.ResourceConflictException;
import andrehsvictor.memorix.common.exception.ResourceNotFoundException;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.deck.dto.CreateDeckDto;
import andrehsvictor.memorix.deck.dto.DeckDto;
import andrehsvictor.memorix.deck.dto.UpdateDeckDto;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;
    private final DeckMapper deckMapper;
    private final JwtService jwtService;
    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;

    public DeckDto toDto(Deck deck) {
        return deckMapper.deckToDeckDto(deck);
    }

    public Page<Deck> getAllWithFilters(
            String query,
            String name,
            String description,
            Boolean includeWithCoverImage,
            Boolean includeEmptyDecks,
            Pageable pageable) {
        UUID userId = jwtService.getCurrentUserUuid();
        return deckRepository.findAllByUserIdWithFilters(
                userId,
                query,
                name,
                description,
                includeWithCoverImage,
                includeEmptyDecks,
                pageable);
    }

    public Deck create(CreateDeckDto createDeckDto) {
        UUID userId = jwtService.getCurrentUserUuid();
        if (deckRepository.existsByNameAndUserId(createDeckDto.getName(), userId)) {
            throw new ResourceConflictException("Deck with name '" + createDeckDto.getName() + "' already exists");
        }
        Deck deck = deckMapper.createDeckDtoToDeck(createDeckDto);
        User user = userService.getById(userId); // Use the userId we already retrieved
        deck.setUser(user);
        return deckRepository.save(deck);
    }

    public Deck getById(UUID id) {
        UUID userId = jwtService.getCurrentUserUuid();
        return deckRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Deck", "ID", id.toString()));
    }

    public Deck update(UUID id, UpdateDeckDto updateDeckDto) {
        Deck deck = getById(id);
        boolean coverImageChanged = updateDeckDto.getCoverImageUrl() != null
                && !updateDeckDto.getCoverImageUrl().equals(deck.getCoverImageUrl());
        String oldCoverImageUrl = deck.getCoverImageUrl();
        deckMapper.updateDeckFromUpdateDeckDto(updateDeckDto, deck);
        deck = deckRepository.save(deck);
        if (coverImageChanged && oldCoverImageUrl != null) {
            rabbitTemplate.convertAndSend("minio.v1.delete.url", oldCoverImageUrl);
        }
        return deck;
    }

    public void delete(UUID id) {
        Deck deck = getById(id);
        deckRepository.delete(deck);
        if (deck.getCoverImageUrl() != null) {
            rabbitTemplate.convertAndSend("minio.v1.delete.url", deck.getCoverImageUrl());
        }
        rabbitTemplate.convertAndSend("cards.v1.deleteAllByDeckId", deck.getId());
    }

    public boolean existsById(UUID id) {
        UUID userId = jwtService.getCurrentUserUuid();
        return deckRepository.existsByIdAndUserId(id, userId);
    }
}
