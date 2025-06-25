package andrehsvictor.memorix.card;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import andrehsvictor.memorix.AbstractIntegrationTest;
import andrehsvictor.memorix.auth.TokenService;
import andrehsvictor.memorix.auth.dto.CredentialsDto;
import andrehsvictor.memorix.auth.dto.TokenDto;
import andrehsvictor.memorix.card.dto.CardDto;
import andrehsvictor.memorix.card.dto.CreateCardDto;
import andrehsvictor.memorix.card.dto.UpdateCardDto;
import andrehsvictor.memorix.deck.Deck;
import andrehsvictor.memorix.deck.DeckRepository;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserRepository;
import andrehsvictor.memorix.user.UserRole;
import io.restassured.http.ContentType;

@DisplayName("Card Controller Integration Tests")
public class CardControllerIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private User anotherUser;
    private String accessToken;
    private Deck testDeck;
    private Deck anotherDeck;
    private Card testCard;
    private Card dueCard;
    private Card anotherUserCard;
    private final String password = "Password123!";

    @BeforeEach
    void setUp() {
        // Clean up databases
        userRepository.deleteAll();
        deckRepository.deleteAll();
        cardRepository.deleteAll();

        // Create test user
        testUser = User.builder()
                .email("test@example.com")
                .username("testuser")
                .displayName("Test User")
                .password(passwordEncoder.encode(password))
                .role(UserRole.USER)
                .emailVerified(true)
                .build();
        testUser = userRepository.save(testUser);

        // Create another user for testing isolation
        anotherUser = User.builder()
                .email("another@example.com")
                .username("anotheruser")
                .displayName("Another User")
                .password(passwordEncoder.encode(password))
                .role(UserRole.USER)
                .emailVerified(true)
                .build();
        anotherUser = userRepository.save(anotherUser);

        // Get access tokens
        CredentialsDto credentials = CredentialsDto.builder()
                .username(testUser.getUsername())
                .password(password)
                .build();
        TokenDto tokenDto = tokenService.request(credentials);
        accessToken = tokenDto.getAccessToken();

        // Create test decks
        testDeck = Deck.builder()
                .name("Test Deck")
                .description("Test Description")
                .color("#FF0000")
                .cardCount(0)
                .user(testUser)
                .build();
        testDeck = deckRepository.save(testDeck);

        anotherDeck = Deck.builder()
                .name("Another Deck")
                .description("Another Description")
                .color("#00FF00")
                .cardCount(0)
                .user(anotherUser)
                .build();
        anotherDeck = deckRepository.save(anotherDeck);

        // Create test cards
        testCard = Card.builder()
                .front("What is Java?")
                .back("Java is a programming language")
                .deckId(testDeck.getId())
                .userId(testUser.getId())
                .interval(5)
                .repetition(2)
                .easeFactor(2.5)
                .reviewCount(3)
                .due(LocalDateTime.now().plusDays(1))
                .build();
        testCard = cardRepository.save(testCard);

        // Create a card that is due
        dueCard = Card.builder()
                .front("What is Python?")
                .back("Python is a programming language")
                .deckId(testDeck.getId())
                .userId(testUser.getId())
                .interval(0)
                .repetition(0)
                .easeFactor(2.5)
                .reviewCount(0)
                .due(LocalDateTime.now().minusHours(1))
                .build();
        dueCard = cardRepository.save(dueCard);

        // Create card for another user
        anotherUserCard = Card.builder()
                .front("What is C++?")
                .back("C++ is a programming language")
                .deckId(anotherDeck.getId())
                .userId(anotherUser.getId())
                .interval(1)
                .repetition(1)
                .easeFactor(2.5)
                .reviewCount(1)
                .due(LocalDateTime.now().plusDays(2))
                .build();
        anotherUserCard = cardRepository.save(anotherUserCard);
    }

    @Test
    @DisplayName("Should return all cards for authenticated user")
    void getAllCards_ShouldReturnUserCards_WhenAuthenticated() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/cards")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(2))
                .body("content[0].front", notNullValue())
                .body("content[0].back", notNullValue())
                .body("content[0].deck.id", equalTo(testDeck.getId().toString()));
    }

    @Test
    @DisplayName("Should return due cards only when due parameter is true")
    void getAllCards_ShouldReturnDueCards_WhenDueIsTrue() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("due", true)
        .when()
                .get("/api/v1/cards")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1))
                .body("content[0].id", equalTo(dueCard.getId().toString()));
    }

    @Test
    @DisplayName("Should return 401 when not authenticated")
    void getAllCards_ShouldReturn401_WhenNotAuthenticated() {
        given()
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/cards")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Should return card statistics for authenticated user")
    void getCardStats_ShouldReturnStats_WhenAuthenticated() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/cards/stats")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("total", equalTo(2))
                .body("due", greaterThanOrEqualTo(0))
                .body("new", greaterThanOrEqualTo(0))
                .body("learning", greaterThanOrEqualTo(0))
                .body("reviewed", greaterThanOrEqualTo(0));
    }

    @Test
    @DisplayName("Should return card statistics by deck ID")
    void getCardStatsByDeckId_ShouldReturnStats_WhenDeckExists() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/decks/{deckId}/cards/stats", testDeck.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("total", equalTo(2));
    }

    @Test
    @DisplayName("Should return 404 when getting stats for non-existent deck")
    void getCardStatsByDeckId_ShouldReturn404_WhenDeckNotExists() {
        UUID nonExistentDeckId = UUID.randomUUID();
        
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/decks/{deckId}/cards/stats", nonExistentDeckId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Should return cards by deck ID")
    void getAllCardsByDeckId_ShouldReturnDeckCards_WhenDeckExists() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/decks/{deckId}/cards", testDeck.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(2));
    }

    @Test
    @DisplayName("Should return due cards by deck ID when due parameter is true")
    void getAllCardsByDeckId_ShouldReturnDueCards_WhenDueIsTrue() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("due", true)
        .when()
                .get("/api/v1/decks/{deckId}/cards", testDeck.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1))
                .body("content[0].id", equalTo(dueCard.getId().toString()));
    }

    @Test
    @DisplayName("Should return 404 when getting cards for non-existent deck")
    void getAllCardsByDeckId_ShouldReturn404_WhenDeckNotExists() {
        UUID nonExistentDeckId = UUID.randomUUID();
        
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/decks/{deckId}/cards", nonExistentDeckId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Should return card by ID when user owns it")
    void getCardById_ShouldReturnCard_WhenUserOwnsIt() {
        CardDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/cards/{cardId}", testCard.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(CardDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testCard.getId().toString());
        assertThat(response.getFront()).isEqualTo("What is Java?");
        assertThat(response.getBack()).isEqualTo("Java is a programming language");
        assertThat(response.getDeck().getId()).isEqualTo(testDeck.getId().toString());
    }

    @Test
    @DisplayName("Should return 404 when card not found or not owned by user")
    void getCardById_ShouldReturn404_WhenCardNotFoundOrNotOwned() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/cards/{cardId}", anotherUserCard.getId())
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Should create card successfully")
    void createCard_ShouldCreateCard_WhenValidData() {
        CreateCardDto createCardDto = CreateCardDto.builder()
                .front("What is JavaScript?")
                .back("JavaScript is a scripting language")
                .build();

        CardDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(createCardDto)
        .when()
                .post("/api/v1/decks/{deckId}/cards", testDeck.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(CardDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getFront()).isEqualTo("What is JavaScript?");
        assertThat(response.getBack()).isEqualTo("JavaScript is a scripting language");
        assertThat(response.getDeck().getId()).isEqualTo(testDeck.getId().toString());
        assertThat(response.getInterval()).isEqualTo(0);
        assertThat(response.getRepetition()).isEqualTo(0);
        assertThat(response.getEaseFactor()).isEqualTo(2.5);

        // Verify card was actually created in database
        Card createdCard = cardRepository.findById(UUID.fromString(response.getId())).orElse(null);
        assertThat(createdCard).isNotNull();
        assertThat(createdCard.getFront()).isEqualTo("What is JavaScript?");
        assertThat(createdCard.getUserId()).isEqualTo(testUser.getId());
        assertThat(createdCard.getDeckId()).isEqualTo(testDeck.getId());
    }

    @Test
    @DisplayName("Should return 400 when creating card with invalid data")
    void createCard_ShouldReturn400_WhenInvalidData() {
        CreateCardDto createCardDto = CreateCardDto.builder()
                .front("") // Invalid: empty front
                .back("Valid back")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(createCardDto)
        .when()
                .post("/api/v1/decks/{deckId}/cards", testDeck.getId())
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should return 404 when creating card in non-existent deck")
    void createCard_ShouldReturn404_WhenDeckNotExists() {
        UUID nonExistentDeckId = UUID.randomUUID();
        CreateCardDto createCardDto = CreateCardDto.builder()
                .front("Valid front")
                .back("Valid back")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(createCardDto)
        .when()
                .post("/api/v1/decks/{deckId}/cards", nonExistentDeckId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Should return 401 when creating card without authentication")
    void createCard_ShouldReturn401_WhenNotAuthenticated() {
        CreateCardDto createCardDto = CreateCardDto.builder()
                .front("Valid front")
                .back("Valid back")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(createCardDto)
        .when()
                .post("/api/v1/decks/{deckId}/cards", testDeck.getId())
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Should update card successfully")
    void updateCard_ShouldUpdateCard_WhenValidData() {
        UpdateCardDto updateCardDto = UpdateCardDto.builder()
                .front("What is Java? (Updated)")
                .back("Java is a programming language (Updated)")
                .build();

        CardDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(updateCardDto)
        .when()
                .put("/api/v1/cards/{cardId}", testCard.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(CardDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getFront()).isEqualTo("What is Java? (Updated)");
        assertThat(response.getBack()).isEqualTo("Java is a programming language (Updated)");

        // Verify card was actually updated in database
        Card updatedCard = cardRepository.findById(testCard.getId()).orElse(null);
        assertThat(updatedCard).isNotNull();
        assertThat(updatedCard.getFront()).isEqualTo("What is Java? (Updated)");
        assertThat(updatedCard.getBack()).isEqualTo("Java is a programming language (Updated)");
    }

    @Test
    @DisplayName("Should return 404 when updating card not owned by user")
    void updateCard_ShouldReturn404_WhenCardNotOwned() {
        UpdateCardDto updateCardDto = UpdateCardDto.builder()
                .front("Updated front")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(updateCardDto)
        .when()
                .put("/api/v1/cards/{cardId}", anotherUserCard.getId())
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Should handle partial updates correctly")
    void updateCard_ShouldHandlePartialUpdates_WhenSomeFieldsProvided() {
        UpdateCardDto updateCardDto = UpdateCardDto.builder()
                .front("What is Java? (Partially Updated)")
                // back not provided (null)
                .build();

        CardDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(updateCardDto)
        .when()
                .put("/api/v1/cards/{cardId}", testCard.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(CardDto.class);

        // Verify only provided fields were updated
        assertThat(response.getFront()).isEqualTo("What is Java? (Partially Updated)");
        assertThat(response.getBack()).isEqualTo("Java is a programming language"); // Should remain unchanged
    }

    @Test
    @DisplayName("Should return 400 when updating card with invalid data")
    void updateCard_ShouldReturn400_WhenInvalidData() {
        UpdateCardDto updateCardDto = UpdateCardDto.builder()
                .front("a".repeat(2001)) // Exceeds max length
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(updateCardDto)
        .when()
                .put("/api/v1/cards/{cardId}", testCard.getId())
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should delete card successfully")
    void deleteCard_ShouldDeleteCard_WhenUserOwnsIt() {
        given()
                .header("Authorization", "Bearer " + accessToken)
        .when()
                .delete("/api/v1/cards/{cardId}", testCard.getId())
        .then()
                .statusCode(HttpStatus.OK.value());

        // Verify card was actually deleted from database
        boolean cardExists = cardRepository.existsById(testCard.getId());
        assertThat(cardExists).isFalse();
    }

    @Test
    @DisplayName("Should return 404 when deleting card not owned by user")
    void deleteCard_ShouldReturn404_WhenCardNotOwned() {
        given()
                .header("Authorization", "Bearer " + accessToken)
        .when()
                .delete("/api/v1/cards/{cardId}", anotherUserCard.getId())
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());

        // Verify card still exists
        boolean cardExists = cardRepository.existsById(anotherUserCard.getId());
        assertThat(cardExists).isTrue();
    }

    @Test
    @DisplayName("Should return 401 when deleting card without authentication")
    void deleteCard_ShouldReturn401_WhenNotAuthenticated() {
        given()
        .when()
                .delete("/api/v1/cards/{cardId}", testCard.getId())
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());

        // Verify card still exists
        boolean cardExists = cardRepository.existsById(testCard.getId());
        assertThat(cardExists).isTrue();
    }

    @Test
    @DisplayName("Should support pagination for cards")
    void getAllCards_ShouldSupportPagination() {
        // Create multiple cards
        for (int i = 1; i <= 15; i++) {
            Card card = Card.builder()
                    .front("Question " + i)
                    .back("Answer " + i)
                    .deckId(testDeck.getId())
                    .userId(testUser.getId())
                    .interval(i % 5)
                    .repetition(i % 3)
                    .easeFactor(2.5)
                    .reviewCount(i)
                    .due(LocalDateTime.now().plusDays(i))
                    .build();
            cardRepository.save(card);
        }

        // Test first page
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 10)
        .when()
                .get("/api/v1/cards")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(10))
                .body("totalElements", equalTo(17)) // 15 created + 2 existing
                .body("totalPages", equalTo(2))
                .body("first", equalTo(true))
                .body("last", equalTo(false));

        // Test second page
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("page", 1)
                .queryParam("size", 10)
        .when()
                .get("/api/v1/cards")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(7)) // Remaining items
                .body("first", equalTo(false))
                .body("last", equalTo(true));
    }

    @Test
    @DisplayName("Should support pagination for cards by deck ID")
    void getAllCardsByDeckId_ShouldSupportPagination() {
        // Create multiple cards
        for (int i = 1; i <= 12; i++) {
            Card card = Card.builder()
                    .front("Question " + i)
                    .back("Answer " + i)
                    .deckId(testDeck.getId())
                    .userId(testUser.getId())
                    .interval(0)
                    .repetition(0)
                    .easeFactor(2.5)
                    .reviewCount(0)
                    .due(LocalDateTime.now().plusDays(1))
                    .build();
            cardRepository.save(card);
        }

        // Test pagination
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 5)
        .when()
                .get("/api/v1/decks/{deckId}/cards", testDeck.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(5))
                .body("totalElements", equalTo(14)) // 12 created + 2 existing
                .body("totalPages", equalTo(3))
                .body("first", equalTo(true))
                .body("last", equalTo(false));
    }

    @Test
    @DisplayName("Should not allow access to cards from another user's deck")
    void getAllCardsByDeckId_ShouldNotReturnCards_WhenDeckBelongsToAnotherUser() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/decks/{deckId}/cards", anotherDeck.getId())
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Should not allow creating cards in another user's deck")
    void createCard_ShouldReturn404_WhenDeckBelongsToAnotherUser() {
        CreateCardDto createCardDto = CreateCardDto.builder()
                .front("Valid front")
                .back("Valid back")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(createCardDto)
        .when()
                .post("/api/v1/decks/{deckId}/cards", anotherDeck.getId())
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

}
