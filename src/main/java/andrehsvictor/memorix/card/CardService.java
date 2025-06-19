package andrehsvictor.memorix.card;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.card.dto.CardDto;
import andrehsvictor.memorix.card.dto.CreateCardDto;
import andrehsvictor.memorix.card.dto.UpdateCardDto;
import andrehsvictor.memorix.common.exception.ResourceNotFoundException;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.deck.DeckService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final DeckService deckService;
    private final JwtService jwtService;
    private final CardMapper cardMapper;

    public CardDto toDto(Card card) {
        return cardMapper.cardToCardDto(card);
    }

    public Page<Card> getAll(Boolean due, Pageable pageable) {
        UUID userId = jwtService.getCurrentUserUuid();
        boolean dueNotNull = due != null;
        if (dueNotNull && due) {
            return cardRepository.findAllByUserIdAndDueBefore(userId, LocalDateTime.now(), pageable);
        }
        return cardRepository.findAllByUserId(userId, pageable);
    }

    public Page<Card> getAllByDeckId(UUID deckId, Boolean due, Pageable pageable) {
        boolean dueNotNull = due != null;
        if (!deckService.existsById(deckId)) {
            throw new ResourceNotFoundException("Deck", "ID", deckId);
        }
        if (dueNotNull && due) {
            return cardRepository.findAllByDeckIdAndDueBefore(deckId, LocalDateTime.now(), pageable);
        }
        return cardRepository.findAllByDeckId(deckId, pageable);
    }

    public Card getById(UUID id) {
        UUID userId = jwtService.getCurrentUserUuid();
        return cardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Card", "ID", id));
    }

    public Card create(UUID deckId, CreateCardDto createCardDto) {
        if (!deckService.existsById(deckId)) {
            throw new ResourceNotFoundException("Deck", "ID", deckId);
        }
        Card card = cardMapper.createCardDtoToCard(createCardDto);
        card.setDeckId(deckId);
        card.setUserId(jwtService.getCurrentUserUuid());
        return cardRepository.save(card);
    }

    public Card update(UUID id, UpdateCardDto updateCardDto) {
        Card card = getById(id);
        cardMapper.updateCardFromUpdateCardDto(updateCardDto, card);
        return cardRepository.save(card);
    }

    public void delete(UUID id) {
        Card card = getById(id);
        cardRepository.delete(card);
    }

    public void deleteAllByDeckId(UUID deckId) {
        
    }

}
