package andrehsvictor.memorix.deck;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

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
import andrehsvictor.memorix.deck.dto.CreateDeckDto;
import andrehsvictor.memorix.deck.dto.DeckDto;
import andrehsvictor.memorix.deck.dto.UpdateDeckDto;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserRepository;
import andrehsvictor.memorix.user.UserRole;
import io.restassured.http.ContentType;

@DisplayName("Deck Controller Integration Tests")
public class DeckControllerIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private User anotherUser;
    private String accessToken;
    private Deck testDeck;
    private Deck anotherDeck;
    private final String password = "Password123!";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        deckRepository.deleteAll();

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
                .coverImageUrl("http://example.com/image.jpg")
                .color("#FF0000")
                .cardCount(5)
                .user(testUser)
                .build();
        testDeck = deckRepository.save(testDeck);

        anotherDeck = Deck.builder()
                .name("Another Deck")
                .description("Another Description")
                .color("#00FF00")
                .cardCount(3)
                .user(anotherUser)
                .build();
        anotherDeck = deckRepository.save(anotherDeck);
    }

    @Test
    @DisplayName("Should return all decks for authenticated user")
    void getAllDecks_ShouldReturnUserDecks_WhenAuthenticated() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/decks")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1))
                .body("content[0].id", equalTo(testDeck.getId().toString()))
                .body("content[0].name", equalTo("Test Deck"))
                .body("content[0].description", equalTo("Test Description"))
                .body("content[0].coverImageUrl", equalTo("http://example.com/image.jpg"))
                .body("content[0].color", equalTo("#FF0000"))
                .body("content[0].cardCount", equalTo(5));
    }

    @Test
    @DisplayName("Should return 401 when not authenticated")
    void getAllDecks_ShouldReturn401_WhenNotAuthenticated() {
        given()
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/decks")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Should filter decks by name")
    void getAllDecks_ShouldFilterByName_WhenNameProvided() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("name", "Test Deck")
        .when()
                .get("/api/v1/decks")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1))
                .body("content[0].name", equalTo("Test Deck"));
    }

    @Test
    @DisplayName("Should filter decks by query")
    void getAllDecks_ShouldFilterByQuery_WhenQueryProvided() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("q", "Test")
        .when()
                .get("/api/v1/decks")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1))
                .body("content[0].name", equalTo("Test Deck"));
    }

    @Test
    @DisplayName("Should filter decks by description")
    void getAllDecks_ShouldFilterByDescription_WhenDescriptionProvided() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("description", "Test Description")
        .when()
                .get("/api/v1/decks")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1))
                .body("content[0].description", equalTo("Test Description"));
    }

    @Test
    @DisplayName("Should filter decks by cover image presence")
    void getAllDecks_ShouldFilterByCoverImage_WhenIncludeWithCoverImageProvided() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("includeWithCoverImage", true)
        .when()
                .get("/api/v1/decks")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1))
                .body("content[0].coverImageUrl", equalTo("http://example.com/image.jpg"));
    }

    @Test
    @DisplayName("Should filter decks by empty status")
    void getAllDecks_ShouldFilterByEmpty_WhenIncludeEmptyProvided() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("includeEmpty", false)
        .when()
                .get("/api/v1/decks")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1))
                .body("content[0].cardCount", equalTo(5));
    }

    @Test
    @DisplayName("Should get deck by ID when user owns it")
    void getDeckById_ShouldReturnDeck_WhenUserOwnsIt() {
        DeckDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/decks/{id}", testDeck.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DeckDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testDeck.getId().toString());
        assertThat(response.getName()).isEqualTo("Test Deck");
        assertThat(response.getDescription()).isEqualTo("Test Description");
        assertThat(response.getCoverImageUrl()).isEqualTo("http://example.com/image.jpg");
        assertThat(response.getColor()).isEqualTo("#FF0000");
        assertThat(response.getCardCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should return 404 when deck not found or not owned by user")
    void getDeckById_ShouldReturn404_WhenDeckNotFoundOrNotOwned() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/decks/{id}", anotherDeck.getId())
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Should create deck successfully")
    void createDeck_ShouldCreateDeck_WhenValidData() {
        CreateDeckDto createDeckDto = CreateDeckDto.builder()
                .name("New Deck")
                .description("New Description")
                .coverImageUrl("http://example.com/new-image.jpg")
                .color("#0000FF")
                .build();

        DeckDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(createDeckDto)
        .when()
                .post("/api/v1/decks")
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DeckDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("New Deck");
        assertThat(response.getDescription()).isEqualTo("New Description");
        assertThat(response.getCoverImageUrl()).isEqualTo("http://example.com/new-image.jpg");
        assertThat(response.getColor()).isEqualTo("#0000FF");
        assertThat(response.getCardCount()).isEqualTo(0);

        // Verify deck was actually created in database
        Deck createdDeck = deckRepository.findById(java.util.UUID.fromString(response.getId())).orElse(null);
        assertThat(createdDeck).isNotNull();
        assertThat(createdDeck.getName()).isEqualTo("New Deck");
        assertThat(createdDeck.getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Should return 400 when creating deck with invalid data")
    void createDeck_ShouldReturn400_WhenInvalidData() {
        CreateDeckDto createDeckDto = CreateDeckDto.builder()
                .name("") // Invalid: empty name
                .description("Valid Description")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(createDeckDto)
        .when()
                .post("/api/v1/decks")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should return 409 when creating deck with duplicate name for same user")
    void createDeck_ShouldReturn409_WhenDuplicateName() {
        CreateDeckDto createDeckDto = CreateDeckDto.builder()
                .name("Test Deck") // Same name as existing deck
                .description("Another Description")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(createDeckDto)
        .when()
                .post("/api/v1/decks")
        .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Should return 401 when creating deck without authentication")
    void createDeck_ShouldReturn401_WhenNotAuthenticated() {
        CreateDeckDto createDeckDto = CreateDeckDto.builder()
                .name("New Deck")
                .description("New Description")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(createDeckDto)
        .when()
                .post("/api/v1/decks")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Should update deck successfully")
    void updateDeck_ShouldUpdateDeck_WhenValidData() {
        UpdateDeckDto updateDeckDto = UpdateDeckDto.builder()
                .name("Updated Deck")
                .description("Updated Description")
                .coverImageUrl("http://example.com/updated-image.jpg")
                .color("#FFFF00")
                .build();

        DeckDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(updateDeckDto)
        .when()
                .put("/api/v1/decks/{id}", testDeck.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DeckDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Updated Deck");
        assertThat(response.getDescription()).isEqualTo("Updated Description");
        assertThat(response.getCoverImageUrl()).isEqualTo("http://example.com/updated-image.jpg");
        assertThat(response.getColor()).isEqualTo("#FFFF00");

        // Verify deck was actually updated in database
        Deck updatedDeck = deckRepository.findById(testDeck.getId()).orElse(null);
        assertThat(updatedDeck).isNotNull();
        assertThat(updatedDeck.getName()).isEqualTo("Updated Deck");
        assertThat(updatedDeck.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    @DisplayName("Should return 404 when updating deck not owned by user")
    void updateDeck_ShouldReturn404_WhenDeckNotOwned() {
        UpdateDeckDto updateDeckDto = UpdateDeckDto.builder()
                .name("Updated Deck")
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(updateDeckDto)
        .when()
                .put("/api/v1/decks/{id}", anotherDeck.getId())
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Should return 400 when updating deck with invalid data")
    void updateDeck_ShouldReturn400_WhenInvalidData() {
        UpdateDeckDto updateDeckDto = UpdateDeckDto.builder()
                .color("invalid-color") // Invalid color format
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(updateDeckDto)
        .when()
                .put("/api/v1/decks/{id}", testDeck.getId())
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should handle partial updates correctly")
    void updateDeck_ShouldHandlePartialUpdates_WhenSomeFieldsProvided() {
        UpdateDeckDto updateDeckDto = UpdateDeckDto.builder()
                .name("Partially Updated Deck")
                // description and coverImageUrl not provided (null)
                .color("#FFFF00")
                .build();

        DeckDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(updateDeckDto)
        .when()
                .put("/api/v1/decks/{id}", testDeck.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(DeckDto.class);

        // Verify only provided fields were updated
        assertThat(response.getName()).isEqualTo("Partially Updated Deck");
        assertThat(response.getDescription()).isEqualTo("Test Description"); // Should remain unchanged
        assertThat(response.getCoverImageUrl()).isEqualTo("http://example.com/image.jpg"); // Should remain unchanged
        assertThat(response.getColor()).isEqualTo("#FFFF00");
    }

    @Test
    @DisplayName("Should delete deck successfully")
    void deleteDeck_ShouldDeleteDeck_WhenUserOwnsIt() {
        given()
                .header("Authorization", "Bearer " + accessToken)
        .when()
                .delete("/api/v1/decks/{id}", testDeck.getId())
        .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Verify deck was actually deleted from database
        boolean deckExists = deckRepository.existsById(testDeck.getId());
        assertThat(deckExists).isFalse();
    }

    @Test
    @DisplayName("Should return 404 when deleting deck not owned by user")
    void deleteDeck_ShouldReturn404_WhenDeckNotOwned() {
        given()
                .header("Authorization", "Bearer " + accessToken)
        .when()
                .delete("/api/v1/decks/{id}", anotherDeck.getId())
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());

        // Verify deck still exists
        boolean deckExists = deckRepository.existsById(anotherDeck.getId());
        assertThat(deckExists).isTrue();
    }

    @Test
    @DisplayName("Should return 401 when deleting deck without authentication")
    void deleteDeck_ShouldReturn401_WhenNotAuthenticated() {
        given()
        .when()
                .delete("/api/v1/decks/{id}", testDeck.getId())
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());

        // Verify deck still exists
        boolean deckExists = deckRepository.existsById(testDeck.getId());
        assertThat(deckExists).isTrue();
    }

    @Test
    @DisplayName("Should support pagination")
    void getAllDecks_ShouldSupportPagination() {
        // Create multiple decks
        for (int i = 1; i <= 15; i++) {
            Deck deck = Deck.builder()
                    .name("Deck " + i)
                    .description("Description " + i)
                    .color("#FFFFFF")
                    .cardCount(i)
                    .user(testUser)
                    .build();
            deckRepository.save(deck);
        }

        // Test first page
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 10)
        .when()
                .get("/api/v1/decks")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(10))
                .body("totalElements", equalTo(16)) // 15 created + 1 existing
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
                .get("/api/v1/decks")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(6)) // Remaining items
                .body("first", equalTo(false))
                .body("last", equalTo(true));
    }

}
