package andrehsvictor.memorix.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import andrehsvictor.memorix.card.CardService;
import andrehsvictor.memorix.card.dto.ReviewCardDto;
import andrehsvictor.memorix.common.exception.BadRequestException;
import andrehsvictor.memorix.common.exception.ResourceNotFoundException;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.review.dto.CreateReviewDto;
import andrehsvictor.memorix.review.dto.ReviewDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService Tests")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private CardService cardService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ReviewService reviewService;

    private UUID testUserId;
    private UUID testCardId;
    private UUID testReviewId;
    private Review testReview;
    private CreateReviewDto createReviewDto;
    private ReviewDto reviewDto;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testCardId = UUID.randomUUID();
        testReviewId = UUID.randomUUID();

        testReview = Review.builder()
                .id(testReviewId)
                .cardId(testCardId)
                .userId(testUserId)
                .rating(4)
                .responseTime(5000)
                .build();

        createReviewDto = CreateReviewDto.builder()
                .rating(4)
                .responseTime(5000)
                .build();

        reviewDto = ReviewDto.builder()
                .id(testReviewId.toString())
                .rating(4)
                .responseTime(5000)
                .build();
    }

    @Test
    @DisplayName("Should convert review to DTO successfully")
    void toDto_ShouldReturnReviewDto() {
        // Given
        when(reviewMapper.reviewToReviewDto(testReview)).thenReturn(reviewDto);

        // When
        ReviewDto result = reviewService.toDto(testReview);

        // Then
        assertThat(result).isEqualTo(reviewDto);
        verify(reviewMapper).reviewToReviewDto(testReview);
    }

    @Test
    @DisplayName("Should get all reviews with filters successfully")
    void getAll_ShouldReturnPageOfReviews() {
        // Given
        Integer minRating = 3;
        Integer maxRating = 5;
        Integer minResponseTime = 1000;
        Integer maxResponseTime = 10000;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> expectedPage = new PageImpl<>(List.of(testReview), pageable, 1);

        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(reviewRepository.findAllByUserIdWithFilters(
                testUserId, minRating, maxRating, minResponseTime, maxResponseTime, pageable))
                .thenReturn(expectedPage);

        // When
        Page<Review> result = reviewService.getAll(minRating, maxRating, minResponseTime, maxResponseTime, pageable);

        // Then
        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testReview);
        verify(jwtService).getCurrentUserUuid();
        verify(reviewRepository).findAllByUserIdWithFilters(
                testUserId, minRating, maxRating, minResponseTime, maxResponseTime, pageable);
    }

    @Test
    @DisplayName("Should throw BadRequestException when minRating > maxRating")
    void getAll_ShouldThrowBadRequestException_WhenInvalidRatingRange() {
        // Given
        Integer minRating = 5;
        Integer maxRating = 3;
        Pageable pageable = PageRequest.of(0, 10);

        // When & Then
        assertThatThrownBy(() -> reviewService.getAll(minRating, maxRating, null, null, pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Minimum rating cannot be greater than maximum rating");
    }

    @Test
    @DisplayName("Should throw BadRequestException when minResponseTime > maxResponseTime")
    void getAll_ShouldThrowBadRequestException_WhenInvalidResponseTimeRange() {
        // Given
        Integer minResponseTime = 10000;
        Integer maxResponseTime = 5000;
        Pageable pageable = PageRequest.of(0, 10);

        // When & Then
        assertThatThrownBy(() -> reviewService.getAll(null, null, minResponseTime, maxResponseTime, pageable))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Minimum response time cannot be greater than maximum response time");
    }

    @Test
    @DisplayName("Should get all reviews by card ID successfully")
    void getAllByCardId_ShouldReturnPageOfReviews() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> expectedPage = new PageImpl<>(List.of(testReview), pageable, 1);

        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(reviewRepository.findAllByCardIdAndUserId(testCardId, testUserId, pageable))
                .thenReturn(expectedPage);

        // When
        Page<Review> result = reviewService.getAllByCardId(testCardId, pageable);

        // Then
        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testReview);
        verify(jwtService).getCurrentUserUuid();
        verify(reviewRepository).findAllByCardIdAndUserId(testCardId, testUserId, pageable);
    }

    @Test
    @DisplayName("Should create review successfully when card exists")
    void create_ShouldCreateReview_WhenCardExists() {
        // Given
        when(cardService.existsById(testCardId)).thenReturn(true);
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(reviewMapper.createReviewDtoToReview(createReviewDto)).thenReturn(testReview);
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        Review result = reviewService.create(testCardId, createReviewDto);

        // Then
        assertThat(result).isEqualTo(testReview);
        assertThat(result.getCardId()).isEqualTo(testCardId);
        assertThat(result.getUserId()).isEqualTo(testUserId);
        verify(cardService).existsById(testCardId);
        verify(jwtService).getCurrentUserUuid();
        verify(reviewMapper).createReviewDtoToReview(createReviewDto);
        verify(reviewRepository).save(any(Review.class));
        verify(rabbitTemplate).convertAndSend(eq("cards.v1.review"), any(ReviewCardDto.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when card does not exist for create")
    void create_ShouldThrowResourceNotFoundException_WhenCardDoesNotExist() {
        // Given
        when(cardService.existsById(testCardId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> reviewService.create(testCardId, createReviewDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Card")
                .hasMessageContaining("ID")
                .hasMessageContaining(testCardId.toString());

        verify(cardService).existsById(testCardId);
    }

    @Test
    @DisplayName("Should get review by ID when review exists")
    void getById_ShouldReturnReview_WhenReviewExists() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(reviewRepository.findByIdAndUserId(testReviewId, testUserId)).thenReturn(Optional.of(testReview));

        // When
        Review result = reviewService.getById(testReviewId);

        // Then
        assertThat(result).isEqualTo(testReview);
        verify(jwtService).getCurrentUserUuid();
        verify(reviewRepository).findByIdAndUserId(testReviewId, testUserId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when review not found")
    void getById_ShouldThrowResourceNotFoundException_WhenReviewNotFound() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(reviewRepository.findByIdAndUserId(testReviewId, testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reviewService.getById(testReviewId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review")
                .hasMessageContaining("ID")
                .hasMessageContaining(testReviewId.toString());

        verify(jwtService).getCurrentUserUuid();
        verify(reviewRepository).findByIdAndUserId(testReviewId, testUserId);
    }
}
