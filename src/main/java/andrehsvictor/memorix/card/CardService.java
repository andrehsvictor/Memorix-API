package andrehsvictor.memorix.card;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.card.dto.PostCardDto;
import andrehsvictor.memorix.card.dto.PutCardDto;
import andrehsvictor.memorix.deck.Deck;
import andrehsvictor.memorix.deck.DeckService;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final DeckService deckService;
    private final CardProcessorFactory cardProcessorFactory;
    private final CardMapper cardMapper;

    public Card create(PostCardDto postCardDto, UUID deckId, UUID userId) {
        Deck deck = deckService.getByIdAndUserId(deckId, userId);
        Card card = cardMapper.postCardDtoToCard(postCardDto);
        CardProcessor cardProcessor = cardProcessorFactory.create(card.getType());
        cardProcessor.process(card);
        card.setDeck(deck);
        deckService.incrementCardsCount(deck);
        return cardRepository.save(card);
    }

    public Card getByIdAndUserId(UUID id, UUID userId) {
        return cardRepository.findByIdAndDeckUserId(id, userId).orElseThrow(
                () -> new ResourceNotFoundException("Card not found with ID '" + id + "'"));
    }

    public Page<Card> getAllToReviewByUserId(UUID userId, Pageable pageable) {
        return cardRepository.findAllByDeckUserIdAndProgressNextRepetitionBefore(userId, LocalDateTime.now(), pageable);
    }

    public Integer countAllToReviewByUserId(UUID userId) {
        return cardRepository.countByDeckUserIdAndProgressNextRepetitionBefore(userId, LocalDateTime.now());
    }

    public void deleteByIdAndUserId(UUID id, UUID userId) {
        Card card = getByIdAndUserId(id, userId);
        cardRepository.deleteById(id);
        deckService.decrementCardsCount(card.getDeck());
    }

    public boolean existsByIdAndUserId(UUID id, UUID userId) {
        return cardRepository.existsByIdAndDeckUserId(id, userId);
    }

    public Card update(UUID id, PutCardDto putCardDto, UUID userId) {
        Card card = getByIdAndUserId(id, userId);
        cardMapper.updateCardDtoFromPutCardDto(putCardDto, card);
        CardProcessor cardProcessor = cardProcessorFactory.create(card.getType());
        cardProcessor.process(card);
        return cardRepository.save(card);
    }

    public Page<Card> getAllByUserIdAndDeckId(UUID userId, UUID deckId, Pageable pageable) {
        return cardRepository.findAllByDeckUserIdAndDeckId(userId, deckId, pageable);
    }

    public Page<Card> getAllByUserId(UUID userId, Pageable pageable) {
        return cardRepository.findAllByDeckUserId(userId, pageable);
    }

}
