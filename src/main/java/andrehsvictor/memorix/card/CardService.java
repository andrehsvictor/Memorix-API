package andrehsvictor.memorix.card;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.card.dto.CardDto;
import andrehsvictor.memorix.card.dto.CardStatsDto;
import andrehsvictor.memorix.card.dto.CreateCardDto;
import andrehsvictor.memorix.card.dto.ReviewCardDto;
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
    private final RabbitTemplate rabbitTemplate;

    public CardDto toDto(Card card) {
        return cardMapper.cardToCardDto(card);
    }

    public CardStatsDto getStats() {
        UUID userId = jwtService.getCurrentUserUuid();
        CardStatsDto stats = cardRepository.findCardStatsByUserId(userId);
        return stats;
    }

    public CardStatsDto getStatsByDeckId(UUID deckId) {
        if (!deckService.existsById(deckId)) {
            throw new ResourceNotFoundException("Deck", "ID", deckId);
        }
        CardStatsDto stats = cardRepository.findCardStatsByDeckId(deckId);
        return stats;
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
        card.setUpdatedAt(LocalDateTime.now());
        return cardRepository.save(card);
    }

    public void delete(UUID id) {
        Card card = getById(id);
        cardRepository.delete(card);
        rabbitTemplate.convertAndSend("reviews.v1.deleteAllByCardId", card.getId());
    }

    public boolean existsById(UUID id) {
        UUID userId = jwtService.getCurrentUserUuid();
        return cardRepository.existsByIdAndUserId(id, userId);
    }

    @RabbitListener(queues = { "cards.v1.delete", "decks.v1.delete" })
    private void deleteAllByDeckId(UUID deckId) {
        cardRepository.deleteByDeckId(deckId);
    }

    @RabbitListener(queues = { "cards.v1.deleteAllByUserId", "users.v1.delete" })
    private void deleteAllByUserId(UUID userId) {
        cardRepository.deleteByUserId(userId);
    }

    @RabbitListener(queues = "cards.v1.review")
    private void review(ReviewCardDto reviewCardDto) {
        Card card = getById(reviewCardDto.getCardId());
        int rating = reviewCardDto.getRating();

        if (rating < 3) {
            card.setRepetition(0);
            card.setInterval(1);
            card.setDue(LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay());
        } else {
            card.setRepetition(card.getRepetition() + 1);

            if (card.getRepetition() == 1) {
                card.setInterval(1);
            } else if (card.getRepetition() == 2) {
                card.setInterval(6);
            } else {
                card.setInterval((int) Math.round(card.getInterval() * card.getEaseFactor()));
            }
            card.setDue(LocalDateTime.now().plusDays(card.getInterval()).toLocalDate().atStartOfDay());
        }
        double newEaseFactor = card.getEaseFactor() + (0.1 - (5 - rating) * (0.08 + (5 - rating) * 0.02));
        if (newEaseFactor < 1.3) {
            newEaseFactor = 1.3;
        }
        card.setEaseFactor(newEaseFactor);
        card.setReviewCount(card.getReviewCount() + 1);
        card.setUpdatedAt(LocalDateTime.now());

        cardRepository.save(card);
    }

}
