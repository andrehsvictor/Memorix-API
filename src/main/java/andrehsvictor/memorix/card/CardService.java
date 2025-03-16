package andrehsvictor.memorix.card;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import andrehsvictor.memorix.card.dto.CardDto;
import andrehsvictor.memorix.card.dto.CreateCardDto;
import andrehsvictor.memorix.card.dto.UpdateCardDto;
import andrehsvictor.memorix.deck.Deck;
import andrehsvictor.memorix.deck.DeckService;
import andrehsvictor.memorix.deckuser.AccessLevel;
import andrehsvictor.memorix.deckuser.DeckUserService;
import andrehsvictor.memorix.exception.ForbiddenOperationException;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.jwt.JwtService;
import andrehsvictor.memorix.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final JwtService jwtService;
    private final UserService userService;
    private final DeckService deckService;
    private final DeckUserService deckUserService;

    public CardDto toDto(Card card) {
        return cardMapper.cardToCardDto(card);
    }

    public Page<Card> findAll(
            String query,
            Boolean author,
            String username,
            Pageable pageable) {
        Long userId = jwtService.getCurrentUserId();
        return cardRepository.findAllAccessibleByUserId(
                query,
                userId,
                author,
                username,
                pageable);
    }

    public Page<Card> findAllByDeckId(
            Long deckId,
            String query,
            Boolean author,
            String username,
            Pageable pageable) {
        Long userId = jwtService.getCurrentUserId();
        return cardRepository.findAllAccessibleOrVisibleByDeckIdAndUserId(
                query,
                deckId,
                userId,
                author,
                username,
                pageable);

    }

    public Card findById(Long id) {
        Long userId = jwtService.getCurrentUserId();
        return cardRepository.findVisibleByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException(Card.class, "ID", id));
    }

    @Transactional
    public Card create(Long deckId, CreateCardDto createCardDto) {
        Long userId = jwtService.getCurrentUserId();
        Deck deck = deckService.findById(deckId);
        boolean isDeckAuthor = deck.getAuthor().getId() == userId;
        boolean hasEditorAccessLevel = deckUserService.hasAccessLevel(deckId, userId, AccessLevel.EDITOR);
        if (!isDeckAuthor && !hasEditorAccessLevel) {
            throw new ForbiddenOperationException("You don't have permission to create a card in this deck");
        }
        Card card = cardMapper.createCardDtoToCard(createCardDto);
        card.setDeck(deck);
        card.setAuthor(userService.findById(userId));
        return cardRepository.save(card);
    }

    @Transactional
    public Card update(Long id, UpdateCardDto updateCardDto) {
        Long userId = jwtService.getCurrentUserId();
        Card card = findById(id);
        boolean isCardAuthor = card.getAuthor().getId().equals(userId);
        boolean hasEditorAccessLevel = deckUserService.hasAccessLevel(card.getDeck().getId(), userId,
                AccessLevel.EDITOR);
        if (!isCardAuthor && !hasEditorAccessLevel) {
            throw new ForbiddenOperationException("You don't have permission to update this card");
        }
        cardMapper.updateCardFromUpdateCardDto(updateCardDto, card);
        return cardRepository.save(card);
    }

    @Transactional
    public void delete(Long id) {
        Long userId = jwtService.getCurrentUserId();
        Card card = findById(id);
        boolean isCardAuthor = card.getAuthor().getId().equals(userId);
        boolean isDeckAuthor = card.getDeck().getAuthor().getId().equals(userId);
        if (!isCardAuthor && !isDeckAuthor) {
            throw new ForbiddenOperationException("You don't have permission to delete this card");
        }
        cardRepository.deleteById(id);
    }

}
