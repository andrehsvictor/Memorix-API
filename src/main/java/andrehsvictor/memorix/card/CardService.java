package andrehsvictor.memorix.card;

import java.util.UUID;

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

    public Card create(PostCardDto postCardDto, String deckSlug, UUID userId) {
        Deck deck = deckService.getBySlugAndUserId(deckSlug, userId);
        Card card = cardMapper.postCardDtoToCard(postCardDto);
        CardProcessor cardProcessor = cardProcessorFactory.get(card.getType());
        cardProcessor.process(card);
        card.setDeck(deck);
        return cardRepository.save(card);
    }

    public Card getById(UUID id) {
        return cardRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Card not found with ID '" + id + "'"));
    }

    public void deleteByIdAndDeckUserId(UUID id, UUID userId) {
        if (!existsByIdAndDeckUserId(id, userId)) {
            throw new ResourceNotFoundException("Card not found with ID '" + id + "'");
        }
        cardRepository.deleteByIdAndDeckUserId(id, userId);
    }

    public boolean existsByIdAndDeckUserId(UUID id, UUID userId) {
        return cardRepository.existsByIdAndDeckUserId(id, userId);
    }

    public Card update(UUID id, PutCardDto putCardDto, UUID userId) {
        if (!existsByIdAndDeckUserId(id, userId)) {
            throw new ResourceNotFoundException("Card not found with ID '" + id + "'");
        }
        Card card = getById(id);
        cardMapper.updateCardDtoFromPutCardDto(putCardDto, card);
        CardProcessor cardProcessor = cardProcessorFactory.get(card.getType());
        cardProcessor.process(card);
        return cardRepository.save(card);
    }

}
