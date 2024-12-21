package andrehsvictor.memorix.card;

import java.util.UUID;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.card.dto.PostCardDto;
import andrehsvictor.memorix.deck.Deck;
import andrehsvictor.memorix.deck.DeckService;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final DeckService deckService;
    private final CardTypeValidatorFactory cardTypeValidatorFactory;
    private final CardMapper cardMapper;

    public Card create(User user, String deckSlug, PostCardDto postCardDto) {
        Deck deck = deckService.getBySlugAndUserId(deckSlug, user.getId());
        Card card = cardMapper.postCardDtoToCard(postCardDto);
        CardTypeValidator cardTypeValidator = cardTypeValidatorFactory.get(card.getType());
        cardTypeValidator.validate(postCardDto);
        card.setDeck(deck);
        return cardRepository.save(card);
    }

    public Card getById(UUID id) {
        return cardRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Card not found with ID '" + id + "'"));
    }

}
