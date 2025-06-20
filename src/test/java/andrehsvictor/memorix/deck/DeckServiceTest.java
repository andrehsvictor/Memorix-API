package andrehsvictor.memorix.deck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import andrehsvictor.memorix.common.exception.ResourceConflictException;
import andrehsvictor.memorix.common.exception.ResourceNotFoundException;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.deck.dto.CreateDeckDto;
import andrehsvictor.memorix.deck.dto.DeckDto;
import andrehsvictor.memorix.deck.dto.UpdateDeckDto;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeckService Tests")
class DeckServiceTest {

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private DeckMapper deckMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private DeckService deckService;

    private UUID testUserId;
    private UUID testDeckId;
    private User testUser;
    private Deck testDeck;
    private CreateDeckDto createDeckDto;
    private UpdateDeckDto updateDeckDto;
    private DeckDto deckDto;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testDeckId = UUID.randomUUID();

        testUser = User.builder()
                .id(testUserId)
                .username("testuser")
                .displayName("Test User")
                .email("test@example.com")
                .build();

        testDeck = Deck.builder()
                .id(testDeckId)
                .name("Test Deck")
                .description("Test Description")
                .coverImageUrl("http://example.com/image.jpg")
                .color("#FF0000")
                .user(testUser)
                .build();

        createDeckDto = CreateDeckDto.builder()
                .name("Test Deck")
                .description("Test Description")
                .coverImageUrl("http://example.com/image.jpg")
                .color("#FF0000")
                .build();

        updateDeckDto = UpdateDeckDto.builder()
                .name("Updated Deck")
                .description("Updated Description")
                .coverImageUrl("http://example.com/updated-image.jpg")
                .build();

        deckDto = DeckDto.builder()
                .id(testDeckId.toString())
                .name("Test Deck")
                .description("Test Description")
                .build();
    }

    @Test
    @DisplayName("Should convert deck to DTO successfully")
    void toDto_ShouldReturnDeckDto() {
        // Given
        when(deckMapper.deckToDeckDto(testDeck)).thenReturn(deckDto);

        // When
        DeckDto result = deckService.toDto(testDeck);

        // Then
        assertThat(result).isEqualTo(deckDto);
        verify(deckMapper).deckToDeckDto(testDeck);
    }

    @Test
    @DisplayName("Should get all decks with filters successfully")
    void getAllWithFilters_ShouldReturnPageOfDecks() {
        // Given
        String query = "test";
        String name = "Test Deck";
        String description = "Test Description";
        Boolean includeWithCoverImage = true;
        Boolean includeEmptyDecks = false;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Deck> expectedPage = new PageImpl<>(List.of(testDeck), pageable, 1);

        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(deckRepository.findAllByUserIdWithFilters(
                testUserId, query, name, description, includeWithCoverImage, includeEmptyDecks, pageable))
                .thenReturn(expectedPage);

        // When
        Page<Deck> result = deckService.getAllWithFilters(
                query, name, description, includeWithCoverImage, includeEmptyDecks, pageable);

        // Then
        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testDeck);
        verify(jwtService).getCurrentUserUuid();
        verify(deckRepository).findAllByUserIdWithFilters(
                testUserId, query, name, description, includeWithCoverImage, includeEmptyDecks, pageable);
    }

    @Test
    @DisplayName("Should create deck successfully when name is unique for user")
    void create_ShouldCreateDeck_WhenNameIsUniqueForUser() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(deckRepository.existsByNameAndUserId(createDeckDto.getName(), testUserId)).thenReturn(false);
        when(deckMapper.createDeckDtoToDeck(createDeckDto)).thenReturn(testDeck);
        when(userService.getById(testUserId)).thenReturn(testUser);
        when(deckRepository.save(any(Deck.class))).thenReturn(testDeck);

        // When
        Deck result = deckService.create(createDeckDto);

        // Then
        assertThat(result).isEqualTo(testDeck);
        verify(jwtService).getCurrentUserUuid();
        verify(deckRepository).existsByNameAndUserId(createDeckDto.getName(), testUserId);
        verify(deckMapper).createDeckDtoToDeck(createDeckDto);
        verify(userService).getById(testUserId);
        verify(deckRepository).save(any(Deck.class));
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when deck name already exists for user")
    void create_ShouldThrowResourceConflictException_WhenNameExistsForUser() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(deckRepository.existsByNameAndUserId(createDeckDto.getName(), testUserId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> deckService.create(createDeckDto))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("Deck with name")
                .hasMessageContaining("already exists");

        verify(jwtService).getCurrentUserUuid();
        verify(deckRepository).existsByNameAndUserId(createDeckDto.getName(), testUserId);
        verify(deckRepository, never()).save(any(Deck.class));
    }

    @Test
    @DisplayName("Should get deck by ID successfully when deck exists for user")
    void getById_ShouldReturnDeck_WhenDeckExistsForUser() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(deckRepository.findByIdAndUserId(testDeckId, testUserId)).thenReturn(Optional.of(testDeck));

        // When
        Deck result = deckService.getById(testDeckId);

        // Then
        assertThat(result).isEqualTo(testDeck);
        verify(jwtService).getCurrentUserUuid();
        verify(deckRepository).findByIdAndUserId(testDeckId, testUserId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deck not found")
    void getById_ShouldThrowResourceNotFoundException_WhenDeckNotFound() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(deckRepository.findByIdAndUserId(testDeckId, testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> deckService.getById(testDeckId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Deck")
                .hasMessageContaining("ID")
                .hasMessageContaining(testDeckId.toString());

        verify(jwtService).getCurrentUserUuid();
        verify(deckRepository).findByIdAndUserId(testDeckId, testUserId);
    }

    @Test
    @DisplayName("Should update deck successfully without cover image change")
    void update_ShouldUpdateDeck_WithoutCoverImageChange() {
        // Given
        updateDeckDto.setCoverImageUrl(testDeck.getCoverImageUrl()); // Same cover image URL

        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(deckRepository.findByIdAndUserId(testDeckId, testUserId)).thenReturn(Optional.of(testDeck));
        when(deckRepository.save(testDeck)).thenReturn(testDeck);

        // When
        Deck result = deckService.update(testDeckId, updateDeckDto);

        // Then
        assertThat(result).isEqualTo(testDeck);
        verify(deckMapper).updateDeckFromUpdateDeckDto(updateDeckDto, testDeck);
        verify(deckRepository).save(testDeck);
        verify(rabbitTemplate, never()).convertAndSend(eq("minio.v1.delete.url"), any(String.class));
    }

    @Test
    @DisplayName("Should update deck and delete old cover image when cover image changes")
    void update_ShouldUpdateDeckAndDeleteOldCoverImage_WhenCoverImageChanges() {
        // Given
        String oldCoverImageUrl = "http://example.com/old-image.jpg";
        testDeck.setCoverImageUrl(oldCoverImageUrl);
        updateDeckDto.setCoverImageUrl("http://example.com/new-image.jpg");

        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(deckRepository.findByIdAndUserId(testDeckId, testUserId)).thenReturn(Optional.of(testDeck));
        when(deckRepository.save(testDeck)).thenReturn(testDeck);

        // When
        Deck result = deckService.update(testDeckId, updateDeckDto);

        // Then
        assertThat(result).isEqualTo(testDeck);
        verify(deckMapper).updateDeckFromUpdateDeckDto(updateDeckDto, testDeck);
        verify(deckRepository).save(testDeck);
        verify(rabbitTemplate).convertAndSend("minio.v1.delete.url", oldCoverImageUrl);
    }

    @Test
    @DisplayName("Should delete deck and send appropriate messages")
    void delete_ShouldDeleteDeckAndSendMessages() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(deckRepository.findByIdAndUserId(testDeckId, testUserId)).thenReturn(Optional.of(testDeck));

        // When
        deckService.delete(testDeckId);

        // Then
        verify(deckRepository).delete(testDeck);
        verify(rabbitTemplate).convertAndSend("minio.v1.delete.url", testDeck.getCoverImageUrl());
        verify(rabbitTemplate).convertAndSend("cards.v1.deleteAllByDeckId", testDeck.getId());
    }

    @Test
    @DisplayName("Should delete deck without sending cover image delete message when no cover image")
    void delete_ShouldDeleteDeckWithoutCoverImageMessage_WhenNoCoverImage() {
        // Given
        testDeck.setCoverImageUrl(null);
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(deckRepository.findByIdAndUserId(testDeckId, testUserId)).thenReturn(Optional.of(testDeck));

        // When
        deckService.delete(testDeckId);

        // Then
        verify(deckRepository).delete(testDeck);
        // verify(rabbitTemplate, never()).convertAndSend(anyString(), any());
        verify(rabbitTemplate).convertAndSend("cards.v1.deleteAllByDeckId", testDeck.getId());
    }

    @Test
    @DisplayName("Should check if deck exists by ID successfully")
    void existsById_ShouldReturnTrue_WhenDeckExistsForUser() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(deckRepository.existsByIdAndUserId(testDeckId, testUserId)).thenReturn(true);

        // When
        boolean result = deckService.existsById(testDeckId);

        // Then
        assertThat(result).isTrue();
        verify(jwtService).getCurrentUserUuid();
        verify(deckRepository).existsByIdAndUserId(testDeckId, testUserId);
    }

    @Test
    @DisplayName("Should return false when deck does not exist for user")
    void existsById_ShouldReturnFalse_WhenDeckDoesNotExistForUser() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(deckRepository.existsByIdAndUserId(testDeckId, testUserId)).thenReturn(false);

        // When
        boolean result = deckService.existsById(testDeckId);

        // Then
        assertThat(result).isFalse();
        verify(jwtService).getCurrentUserUuid();
        verify(deckRepository).existsByIdAndUserId(testDeckId, testUserId);
    }
}
