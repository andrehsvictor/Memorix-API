package andrehsvictor.memorix.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import andrehsvictor.memorix.common.exception.BadRequestException;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.common.minio.MinioService;
import andrehsvictor.memorix.image.dto.ImageDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("ImageService Tests")
class ImageServiceTest {

    @Mock
    private MinioService minioService;

    @Mock
    private JwtService jwtService;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private ImageService imageService;

    private UUID testUserId;
    private String testImageUrl;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testImageUrl = "http://example.com/uploaded-image.jpg";
    }

    @Test
    @DisplayName("Should upload image successfully with valid file")
    void upload_ShouldReturnImageDto_WhenFileValid() {
        // Given
        when(file.getSize()).thenReturn(5L * 1024 * 1024); // 5MB
        when(file.getContentType()).thenReturn("image/jpeg");
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(minioService.upload(eq(file), any())).thenReturn(testImageUrl);

        // When
        ImageDto result = imageService.upload(file);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUrl()).isEqualTo(testImageUrl);
        
        verify(jwtService).getCurrentUserUuid();
        verify(minioService).upload(eq(file), eq(Map.of("userId", testUserId.toString())));
    }

    @Test
    @DisplayName("Should throw BadRequestException when file size exceeds limit")
    void upload_ShouldThrowBadRequestException_WhenFileTooLarge() {
        // Given
        when(file.getSize()).thenReturn(15L * 1024 * 1024); // 15MB (exceeds 10MB limit)

        // When & Then
        assertThatThrownBy(() -> imageService.upload(file))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("File size exceeds the maximum limit of 10MB");
    }

    @Test
    @DisplayName("Should throw BadRequestException when file is not an image")
    void upload_ShouldThrowBadRequestException_WhenFileNotImage() {
        // Given
        when(file.getSize()).thenReturn(5L * 1024 * 1024); // 5MB
        when(file.getContentType()).thenReturn("application/pdf");

        // When & Then
        assertThatThrownBy(() -> imageService.upload(file))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid file type. Only image files are allowed.");
    }

    @Test
    @DisplayName("Should throw BadRequestException when content type is null")
    void upload_ShouldThrowBadRequestException_WhenContentTypeNull() {
        // Given
        when(file.getSize()).thenReturn(5L * 1024 * 1024); // 5MB
        when(file.getContentType()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> imageService.upload(file))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid file type. Only image files are allowed.");
    }

    @Test
    @DisplayName("Should handle different image types")
    void upload_ShouldWork_WithDifferentImageTypes() {
        // Given
        when(file.getSize()).thenReturn(3L * 1024 * 1024); // 3MB
        when(file.getContentType()).thenReturn("image/png");
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(minioService.upload(eq(file), any())).thenReturn(testImageUrl);

        // When
        ImageDto result = imageService.upload(file);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUrl()).isEqualTo(testImageUrl);
        
        verify(jwtService).getCurrentUserUuid();
        verify(minioService).upload(eq(file), eq(Map.of("userId", testUserId.toString())));
    }
}
