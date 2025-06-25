package andrehsvictor.memorix.review.it;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

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
import andrehsvictor.memorix.card.Card;
import andrehsvictor.memorix.card.CardRepository;
import andrehsvictor.memorix.deck.Deck;
import andrehsvictor.memorix.deck.DeckRepository;
import andrehsvictor.memorix.review.Review;
import andrehsvictor.memorix.review.ReviewRepository;
import andrehsvictor.memorix.review.dto.CreateReviewDto;
import andrehsvictor.memorix.review.dto.ReviewDto;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserRepository;
import andrehsvictor.memorix.user.UserRole;
import io.restassured.http.ContentType;

@DisplayName("Review Controller Integration Tests")
public class ReviewControllerIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private ReviewRepository reviewRepository;

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
    private Card testCard2;
    private Card anotherUserCard;
    private Review testReview;
    private Review testReview2;
    private Review anotherUserReview;
    private final String password = "Password123!";

    @BeforeEach
    void setUp() {
        // Clean up databases
        userRepository.deleteAll();
        deckRepository.deleteAll();
        cardRepository.deleteAll();
        reviewRepository.deleteAll();

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
                .repetition(1)
                .easeFactor(2.5)
                .reviewCount(0)
                .due(LocalDateTime.now().plusDays(1))
                .build();
        testCard = cardRepository.save(testCard);

        testCard2 = Card.builder()
                .front("What is Python?")
                .back("Python is a programming language")
                .deckId(testDeck.getId())
                .userId(testUser.getId())
                .interval(3)
                .repetition(2)
                .easeFactor(2.6)
                .reviewCount(1)
                .due(LocalDateTime.now().plusDays(2))
                .build();
        testCard2 = cardRepository.save(testCard2);

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

        // Create test reviews
        testReview = Review.builder()
                .cardId(testCard.getId())
                .userId(testUser.getId())
                .rating(4)
                .responseTime(3500)
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();
        testReview = reviewRepository.save(testReview);

        testReview2 = Review.builder()
                .cardId(testCard2.getId())
                .userId(testUser.getId())
                .rating(5)
                .responseTime(2000)
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();
        testReview2 = reviewRepository.save(testReview2);

        anotherUserReview = Review.builder()
                .cardId(anotherUserCard.getId())
                .userId(anotherUser.getId())
                .rating(3)
                .responseTime(4500)
                .createdAt(LocalDateTime.now().minusHours(3))
                .build();
        anotherUserReview = reviewRepository.save(anotherUserReview);
    }

    @Test
    @DisplayName("Should return all reviews for authenticated user")
    void getAllReviews_ShouldReturnUserReviews_WhenAuthenticated() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/reviews")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(2))
                .body("content[0].rating", notNullValue())
                .body("content[0].responseTime", notNullValue())
                .body("content[0].card.id", notNullValue())
                .body("totalElements", equalTo(2));
    }

    @Test
    @DisplayName("Should return 401 when not authenticated")
    void getAllReviews_ShouldReturn401_WhenNotAuthenticated() {
        given()
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/reviews")
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Should filter reviews by rating range")
    void getAllReviews_ShouldFilterByRating_WhenRatingFiltersProvided() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("minRating", 4)
                .queryParam("maxRating", 5)
        .when()
                .get("/api/v1/reviews")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(2))
                .body("totalElements", equalTo(2));

        // Test with more restrictive filter
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("minRating", 5)
                .queryParam("maxRating", 5)
        .when()
                .get("/api/v1/reviews")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1))
                .body("content[0].rating", equalTo(5));
    }

    @Test
    @DisplayName("Should filter reviews by response time range")
    void getAllReviews_ShouldFilterByResponseTime_WhenResponseTimeFiltersProvided() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("minResponseTime", 2000)
                .queryParam("maxResponseTime", 3000)
        .when()
                .get("/api/v1/reviews")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(1))
                .body("content[0].responseTime", equalTo(2000));
    }

    @Test
    @DisplayName("Should return 400 when minRating > maxRating")
    void getAllReviews_ShouldReturn400_WhenInvalidRatingRange() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("minRating", 5)
                .queryParam("maxRating", 3)
        .when()
                .get("/api/v1/reviews")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should return 400 when minResponseTime > maxResponseTime")
    void getAllReviews_ShouldReturn400_WhenInvalidResponseTimeRange() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("minResponseTime", 5000)
                .queryParam("maxResponseTime", 2000)
        .when()
                .get("/api/v1/reviews")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should support pagination for reviews")
    void getAllReviews_ShouldSupportPagination() {
        // Create additional reviews for pagination testing
        for (int i = 1; i <= 10; i++) {
            Review review = Review.builder()
                    .cardId(testCard.getId())
                    .userId(testUser.getId())
                    .rating(i % 5 + 1)
                    .responseTime(1000 + (i * 100))
                    .createdAt(LocalDateTime.now().minusMinutes(i))
                    .build();
            reviewRepository.save(review);
        }

        // Test first page
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 5)
        .when()
                .get("/api/v1/reviews")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(5))
                .body("totalElements", equalTo(12)) // 2 existing + 10 created
                .body("totalPages", equalTo(3))
                .body("first", equalTo(true))
                .body("last", equalTo(false));

        // Test second page
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("page", 1)
                .queryParam("size", 5)
        .when()
                .get("/api/v1/reviews")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(5))
                .body("first", equalTo(false))
                .body("last", equalTo(false));
    }

    @Test
    @DisplayName("Should return reviews by card ID")
    void getReviewsByCardId_ShouldReturnCardReviews_WhenCardExists() {
        // Create additional reviews for the same card
        Review additionalReview = Review.builder()
                .cardId(testCard.getId())
                .userId(testUser.getId())
                .rating(3)
                .responseTime(4000)
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .build();
        reviewRepository.save(additionalReview);

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/cards/{cardId}/reviews", testCard.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(2))
                .body("content[0].card.id", equalTo(testCard.getId().toString()))
                .body("content[1].card.id", equalTo(testCard.getId().toString()));
    }

    @Test
    @DisplayName("Should return empty page when card has no reviews")
    void getReviewsByCardId_ShouldReturnEmptyPage_WhenCardHasNoReviews() {
        // Create a new card with no reviews
        Card newCard = Card.builder()
                .front("New Question")
                .back("New Answer")
                .deckId(testDeck.getId())
                .userId(testUser.getId())
                .interval(0)
                .repetition(0)
                .easeFactor(2.5)
                .reviewCount(0)
                .due(LocalDateTime.now())
                .build();
        newCard = cardRepository.save(newCard);

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/cards/{cardId}/reviews", newCard.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(0))
                .body("totalElements", equalTo(0));
    }

    @Test
    @DisplayName("Should return 401 when getting reviews by card ID without authentication")
    void getReviewsByCardId_ShouldReturn401_WhenNotAuthenticated() {
        given()
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/cards/{cardId}/reviews", testCard.getId())
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Should not return reviews for another user's card")
    void getReviewsByCardId_ShouldNotReturnReviews_WhenCardBelongsToAnotherUser() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/cards/{cardId}/reviews", anotherUserCard.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(0))
                .body("totalElements", equalTo(0));
    }

    @Test
    @DisplayName("Should support pagination for reviews by card ID")
    void getReviewsByCardId_ShouldSupportPagination() {
        // Create multiple reviews for the same card
        for (int i = 1; i <= 8; i++) {
            Review review = Review.builder()
                    .cardId(testCard.getId())
                    .userId(testUser.getId())
                    .rating(i % 5 + 1)
                    .responseTime(1500 + (i * 200))
                    .createdAt(LocalDateTime.now().minusMinutes(i * 5))
                    .build();
            reviewRepository.save(review);
        }

        // Test pagination
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("page", 0)
                .queryParam("size", 4)
        .when()
                .get("/api/v1/cards/{cardId}/reviews", testCard.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(4))
                .body("totalElements", equalTo(9)) // 1 existing + 8 created
                .body("totalPages", equalTo(3))
                .body("first", equalTo(true))
                .body("last", equalTo(false));
    }

    @Test
    @DisplayName("Should create review successfully")
    void createReview_ShouldCreateReview_WhenValidData() {
        CreateReviewDto createReviewDto = CreateReviewDto.builder()
                .rating(4)
                .responseTime(2500)
                .build();

        ReviewDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(createReviewDto)
        .when()
                .post("/api/v1/cards/{cardId}/reviews", testCard.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(ReviewDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getRating()).isEqualTo(4);
        assertThat(response.getResponseTime()).isEqualTo(2500);
        assertThat(response.getCard().getId()).isEqualTo(testCard.getId().toString());
        assertThat(response.getId()).isNotNull();
        assertThat(response.getCreatedAt()).isNotNull();

        // Verify review was actually created in database
        Review createdReview = reviewRepository.findById(UUID.fromString(response.getId())).orElse(null);
        assertThat(createdReview).isNotNull();
        assertThat(createdReview.getRating()).isEqualTo(4);
        assertThat(createdReview.getResponseTime()).isEqualTo(2500);
        assertThat(createdReview.getUserId()).isEqualTo(testUser.getId());
        assertThat(createdReview.getCardId()).isEqualTo(testCard.getId());
    }

    @Test
    @DisplayName("Should return 400 when creating review with invalid data")
    void createReview_ShouldReturn400_WhenInvalidData() {
        // Test with rating too high
        CreateReviewDto invalidRating = CreateReviewDto.builder()
                .rating(6) // Invalid: exceeds max value
                .responseTime(2500)
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(invalidRating)
        .when()
                .post("/api/v1/cards/{cardId}/reviews", testCard.getId())
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        // Test with negative rating
        CreateReviewDto negativeRating = CreateReviewDto.builder()
                .rating(-1) // Invalid: below min value
                .responseTime(2500)
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(negativeRating)
        .when()
                .post("/api/v1/cards/{cardId}/reviews", testCard.getId())
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        // Test with invalid response time
        CreateReviewDto invalidResponseTime = CreateReviewDto.builder()
                .rating(4)
                .responseTime(0) // Invalid: below min value
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(invalidResponseTime)
        .when()
                .post("/api/v1/cards/{cardId}/reviews", testCard.getId())
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should return 400 when creating review with null values")
    void createReview_ShouldReturn400_WhenNullValues() {
        // Test with null rating
        CreateReviewDto nullRating = CreateReviewDto.builder()
                .rating(null)
                .responseTime(2500)
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(nullRating)
        .when()
                .post("/api/v1/cards/{cardId}/reviews", testCard.getId())
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        // Test with null response time
        CreateReviewDto nullResponseTime = CreateReviewDto.builder()
                .rating(4)
                .responseTime(null)
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(nullResponseTime)
        .when()
                .post("/api/v1/cards/{cardId}/reviews", testCard.getId())
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should return 404 when creating review for non-existent card")
    void createReview_ShouldReturn404_WhenCardNotExists() {
        UUID nonExistentCardId = UUID.randomUUID();
        CreateReviewDto createReviewDto = CreateReviewDto.builder()
                .rating(4)
                .responseTime(2500)
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(createReviewDto)
        .when()
                .post("/api/v1/cards/{cardId}/reviews", nonExistentCardId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Should return 404 when creating review for another user's card")
    void createReview_ShouldReturn404_WhenCardBelongsToAnotherUser() {
        CreateReviewDto createReviewDto = CreateReviewDto.builder()
                .rating(4)
                .responseTime(2500)
                .build();

        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(createReviewDto)
        .when()
                .post("/api/v1/cards/{cardId}/reviews", anotherUserCard.getId())
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Should return 401 when creating review without authentication")
    void createReview_ShouldReturn401_WhenNotAuthenticated() {
        CreateReviewDto createReviewDto = CreateReviewDto.builder()
                .rating(4)
                .responseTime(2500)
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(createReviewDto)
        .when()
                .post("/api/v1/cards/{cardId}/reviews", testCard.getId())
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Should create review with minimum valid values")
    void createReview_ShouldCreateReview_WhenMinimumValidValues() {
        CreateReviewDto createReviewDto = CreateReviewDto.builder()
                .rating(0) // Minimum valid rating
                .responseTime(1) // Minimum valid response time
                .build();

        ReviewDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(createReviewDto)
        .when()
                .post("/api/v1/cards/{cardId}/reviews", testCard.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(ReviewDto.class);

        assertThat(response.getRating()).isEqualTo(0);
        assertThat(response.getResponseTime()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should create review with maximum valid values")
    void createReview_ShouldCreateReview_WhenMaximumValidValues() {
        CreateReviewDto createReviewDto = CreateReviewDto.builder()
                .rating(5) // Maximum valid rating
                .responseTime(999999) // Large but valid response time
                .build();

        ReviewDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(createReviewDto)
        .when()
                .post("/api/v1/cards/{cardId}/reviews", testCard.getId())
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(ReviewDto.class);

        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.getResponseTime()).isEqualTo(999999);
    }

    @Test
    @DisplayName("Should return reviews with card information populated")
    void getAllReviews_ShouldReturnReviewsWithCardInfo() {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
        .when()
                .get("/api/v1/reviews")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(2))
                .body("content[0].card.id", notNullValue())
                .body("content[0].card.front", notNullValue())
                .body("content[0].card.back", notNullValue())
                .body("content[0].card.deck.id", equalTo(testDeck.getId().toString()))
                .body("content[1].card.id", notNullValue())
                .body("content[1].card.front", notNullValue())
                .body("content[1].card.back", notNullValue())
                .body("content[1].card.deck.id", equalTo(testDeck.getId().toString()));
    }

    @Test
    @DisplayName("Should handle complex filtering scenarios")
    void getAllReviews_ShouldHandleComplexFilters() {
        // Create reviews with specific criteria for complex filtering
        Review lowRatingSlowResponse = Review.builder()
                .cardId(testCard.getId())
                .userId(testUser.getId())
                .rating(2)
                .responseTime(8000)
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .build();
        reviewRepository.save(lowRatingSlowResponse);

        Review highRatingFastResponse = Review.builder()
                .cardId(testCard.getId())
                .userId(testUser.getId())
                .rating(5)
                .responseTime(1000)
                .createdAt(LocalDateTime.now().minusMinutes(5))
                .build();
        reviewRepository.save(highRatingFastResponse);

        // Test filtering for high rating and fast response
        given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .queryParam("minRating", 4)
                .queryParam("minResponseTime", 1000)
                .queryParam("maxResponseTime", 2000)
        .when()
                .get("/api/v1/reviews")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(2)) // testReview2 (rating=5, responseTime=2000) and highRatingFastResponse
                .body("totalElements", equalTo(2));
    }

}
