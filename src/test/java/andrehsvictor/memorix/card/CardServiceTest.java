package andrehsvictor.memorix.card;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import andrehsvictor.memorix.card.dto.CardDto;
import andrehsvictor.memorix.card.dto.CardStatsDto;
import andrehsvictor.memorix.card.dto.CreateCardDto;
import andrehsvictor.memorix.card.dto.UpdateCardDto;
import andrehsvictor.memorix.common.exception.ResourceNotFoundException;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.deck.DeckService;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardService Tests")
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private DeckService deckService;

    @Mock
    private JwtService jwtService;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private CardService cardService;

    private UUID testUserId;
    private UUID testDeckId;
    private UUID testCardId;
    private Card testCard;
    private CreateCardDto createCardDto;
    private UpdateCardDto updateCardDto;
    private CardDto cardDto;
    private CardStatsDto cardStatsDto;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testDeckId = UUID.randomUUID();
        testCardId = UUID.randomUUID();

        testCard = Card.builder()
                .id(testCardId)
                .front("Test Question")
                .back("Test Answer")
                .deckId(testDeckId)
                .userId(testUserId)
                .interval(1)
                .repetition(0)
                .easeFactor(2.5)
                .reviewCount(0)
                .due(LocalDateTime.now().plusDays(1))
                .build();

        createCardDto = CreateCardDto.builder()
                .front("Test Question")
                .back("Test Answer")
                .build();

        updateCardDto = UpdateCardDto.builder()
                .front("Updated Question")
                .back("Updated Answer")
                .build();

        cardDto = CardDto.builder()
                .id(testCardId.toString())
                .front("Test Question")
                .back("Test Answer")
                .build();

        cardStatsDto = CardStatsDto.builder()
                .total(10L)
                .due(5L)
                .reviewed(3L)
                .newCards(2L)
                .learning(1L)
                .build();
    }

    @Test
    @DisplayName("Should convert card to DTO successfully")
    void toDto_ShouldReturnCardDto() {
        // Given
        when(cardMapper.cardToCardDto(testCard)).thenReturn(cardDto);

        // When
        CardDto result = cardService.toDto(testCard);

        // Then
        assertThat(result).isEqualTo(cardDto);
        verify(cardMapper).cardToCardDto(testCard);
    }

    @Test
    @DisplayName("Should get stats successfully")
    void getStats_ShouldReturnCardStats() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(cardRepository.findCardStatsByUserId(testUserId)).thenReturn(cardStatsDto);

        // When
        CardStatsDto result = cardService.getStats();

        // Then
        assertThat(result).isEqualTo(cardStatsDto);
        verify(jwtService).getCurrentUserUuid();
        verify(cardRepository).findCardStatsByUserId(testUserId);
    }

    @Test
    @DisplayName("Should get stats by deck ID when deck exists")
    void getStatsByDeckId_ShouldReturnCardStats_WhenDeckExists() {
        // Given
        when(deckService.existsById(testDeckId)).thenReturn(true);
        when(cardRepository.findCardStatsByDeckId(testDeckId)).thenReturn(cardStatsDto);

        // When
        CardStatsDto result = cardService.getStatsByDeckId(testDeckId);

        // Then
        assertThat(result).isEqualTo(cardStatsDto);
        verify(deckService).existsById(testDeckId);
        verify(cardRepository).findCardStatsByDeckId(testDeckId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deck does not exist for stats")
    void getStatsByDeckId_ShouldThrowResourceNotFoundException_WhenDeckDoesNotExist() {
        // Given
        when(deckService.existsById(testDeckId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> cardService.getStatsByDeckId(testDeckId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Deck")
                .hasMessageContaining("ID")
                .hasMessageContaining(testDeckId.toString());

        verify(deckService).existsById(testDeckId);
        verify(cardRepository, never()).findCardStatsByDeckId(any());
    }

    @Test
    @DisplayName("Should get all cards with due filter")
    void getAll_ShouldReturnDueCards_WhenDueIsTrue() {
        // Given
        Boolean due = true;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> expectedPage = new PageImpl<>(List.of(testCard), pageable, 1);

        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(cardRepository.findAllByUserIdAndDueBefore(eq(testUserId), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(expectedPage);

        // When
        Page<Card> result = cardService.getAll(due, pageable);

        // Then
        assertThat(result).isEqualTo(expectedPage);
        verify(jwtService).getCurrentUserUuid();
        verify(cardRepository).findAllByUserIdAndDueBefore(eq(testUserId), any(LocalDateTime.class), eq(pageable));
    }

    @Test
    @DisplayName("Should get all cards without due filter")
    void getAll_ShouldReturnAllCards_WhenDueIsNull() {
        // Given
        Boolean due = null;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> expectedPage = new PageImpl<>(List.of(testCard), pageable, 1);

        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(cardRepository.findAllByUserId(testUserId, pageable)).thenReturn(expectedPage);

        // When
        Page<Card> result = cardService.getAll(due, pageable);

        // Then
        assertThat(result).isEqualTo(expectedPage);
        verify(jwtService).getCurrentUserUuid();
        verify(cardRepository).findAllByUserId(testUserId, pageable);
    }

    @Test
    @DisplayName("Should get all cards by deck ID when deck exists")
    void getAllByDeckId_ShouldReturnCards_WhenDeckExists() {
        // Given
        Boolean due = false;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> expectedPage = new PageImpl<>(List.of(testCard), pageable, 1);

        when(deckService.existsById(testDeckId)).thenReturn(true);
        when(cardRepository.findAllByDeckId(testDeckId, pageable)).thenReturn(expectedPage);

        // When
        Page<Card> result = cardService.getAllByDeckId(testDeckId, due, pageable);

        // Then
        assertThat(result).isEqualTo(expectedPage);
        verify(deckService).existsById(testDeckId);
        verify(cardRepository).findAllByDeckId(testDeckId, pageable);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deck does not exist for getAllByDeckId")
    void getAllByDeckId_ShouldThrowResourceNotFoundException_WhenDeckDoesNotExist() {
        // Given
        Boolean due = false;
        Pageable pageable = PageRequest.of(0, 10);
        when(deckService.existsById(testDeckId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> cardService.getAllByDeckId(testDeckId, due, pageable))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Deck")
                .hasMessageContaining("ID")
                .hasMessageContaining(testDeckId.toString());

        verify(deckService).existsById(testDeckId);
        verify(cardRepository, never()).findAllByDeckId(any(), any());
    }

    @Test
    @DisplayName("Should get card by ID when card exists")
    void getById_ShouldReturnCard_WhenCardExists() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(cardRepository.findByIdAndUserId(testCardId, testUserId)).thenReturn(Optional.of(testCard));

        // When
        Card result = cardService.getById(testCardId);

        // Then
        assertThat(result).isEqualTo(testCard);
        verify(jwtService).getCurrentUserUuid();
        verify(cardRepository).findByIdAndUserId(testCardId, testUserId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when card not found")
    void getById_ShouldThrowResourceNotFoundException_WhenCardNotFound() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(cardRepository.findByIdAndUserId(testCardId, testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cardService.getById(testCardId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Card")
                .hasMessageContaining("ID")
                .hasMessageContaining(testCardId.toString());

        verify(jwtService).getCurrentUserUuid();
        verify(cardRepository).findByIdAndUserId(testCardId, testUserId);
    }

    @Test
    @DisplayName("Should create card successfully when deck exists")
    void create_ShouldCreateCard_WhenDeckExists() {
        // Given
        when(deckService.existsById(testDeckId)).thenReturn(true);
        when(cardMapper.createCardDtoToCard(createCardDto)).thenReturn(testCard);
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        // When
        Card result = cardService.create(testDeckId, createCardDto);

        // Then
        assertThat(result).isEqualTo(testCard);
        assertThat(result.getDeckId()).isEqualTo(testDeckId);
        assertThat(result.getUserId()).isEqualTo(testUserId);
        verify(deckService).existsById(testDeckId);
        verify(cardMapper).createCardDtoToCard(createCardDto);
        verify(jwtService).getCurrentUserUuid();
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deck does not exist for create")
    void create_ShouldThrowResourceNotFoundException_WhenDeckDoesNotExist() {
        // Given
        when(deckService.existsById(testDeckId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> cardService.create(testDeckId, createCardDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Deck")
                .hasMessageContaining("ID")
                .hasMessageContaining(testDeckId.toString());

        verify(deckService).existsById(testDeckId);
        verify(cardRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update card successfully")
    void update_ShouldUpdateCard() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(cardRepository.findByIdAndUserId(testCardId, testUserId)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(testCard)).thenReturn(testCard);

        // When
        Card result = cardService.update(testCardId, updateCardDto);

        // Then
        assertThat(result).isEqualTo(testCard);
        verify(cardMapper).updateCardFromUpdateCardDto(updateCardDto, testCard);
        verify(cardRepository).save(testCard);
    }

    @Test
    @DisplayName("Should delete card and send review deletion message")
    void delete_ShouldDeleteCardAndSendMessage() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(cardRepository.findByIdAndUserId(testCardId, testUserId)).thenReturn(Optional.of(testCard));

        // When
        cardService.delete(testCardId);

        // Then
        verify(cardRepository).delete(testCard);
        verify(rabbitTemplate).convertAndSend("reviews.v1.deleteAllByCardId", testCard.getId());
    }

    @Test
    @DisplayName("Should check if card exists by ID")
    void existsById_ShouldReturnTrue_WhenCardExists() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(cardRepository.existsByIdAndUserId(testCardId, testUserId)).thenReturn(true);

        // When
        boolean result = cardService.existsById(testCardId);

        // Then
        assertThat(result).isTrue();
        verify(jwtService).getCurrentUserUuid();
        verify(cardRepository).existsByIdAndUserId(testCardId, testUserId);
    }
}
