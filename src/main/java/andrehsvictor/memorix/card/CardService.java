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
import andrehsvictor.memorix.exception.ForbiddenActionException;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.progress.Progress;
import andrehsvictor.memorix.progress.ProgressService;
import andrehsvictor.memorix.review.ReviewService;
import andrehsvictor.memorix.review.dto.PostReviewDto;
import andrehsvictor.memorix.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final DeckService deckService;
    private final CardProcessorFactory cardProcessorFactory;
    private final ProgressService progressService;
    private final ReviewService reviewService;
    private final CardMapper cardMapper;

    public Card create(PostCardDto postCardDto, String deckSlug, User user) {
        Deck deck = deckService.getBySlugAndUserId(deckSlug, user.getId());
        Card card = cardMapper.postCardDtoToCard(postCardDto);
        CardProcessor cardProcessor = cardProcessorFactory.create(card.getType());
        cardProcessor.process(card);
        card.setDeck(deck);
        deckService.incrementCardsCount(deck);
        card = cardRepository.save(card);
        progressService.create(user, card);
        return card;
    }

    public void review(UUID id, PostReviewDto postReviewDto, User user) {
        Card card = getByIdAndDeckUserId(id, user.getId());
        Progress progress = progressService.getByUserIdAndCardId(user.getId(), id);
        if (progress.getNextRepetition().isAfter(LocalDateTime.now())) {
            throw new ForbiddenActionException("You can't review this card yet");
        }
        reviewService.create(postReviewDto, user, card);
        progressService.progress(progress, postReviewDto);
    }

    public Card getByIdAndDeckUserId(UUID id, UUID userId) {
        return cardRepository.findByIdAndDeckUserId(id, userId).orElseThrow(
                () -> new ResourceNotFoundException("Card not found with ID '" + id + "'"));
    }

    public void deleteByIdAndDeckUserId(UUID id, UUID userId) {
        Card card = getByIdAndDeckUserId(id, userId);
        cardRepository.deleteById(id);
        deckService.decrementCardsCount(card.getDeck());
    }

    public boolean existsByIdAndDeckUserId(UUID id, UUID userId) {
        return cardRepository.existsByIdAndDeckUserId(id, userId);
    }

    public Card update(UUID id, PutCardDto putCardDto, UUID userId) {
        Card card = getByIdAndDeckUserId(id, userId);
        cardMapper.updateCardDtoFromPutCardDto(putCardDto, card);
        CardProcessor cardProcessor = cardProcessorFactory.create(card.getType());
        cardProcessor.process(card);
        return cardRepository.save(card);
    }

    public Page<Card> getAllByDeckUserIdAndDeckSlug(UUID userId, String deckSlug, Pageable pageable) {
        return cardRepository.findAllByDeckUserIdAndDeckSlug(userId, deckSlug, pageable);
    }

    public Page<Card> getAllByDeckUserId(UUID userId, Pageable pageable) {
        return cardRepository.findAllByDeckUserId(userId, pageable);
    }

}
