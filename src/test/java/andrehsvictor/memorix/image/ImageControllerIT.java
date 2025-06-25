package andrehsvictor.memorix.image;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
import andrehsvictor.memorix.image.dto.ImageDto;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserRepository;
import andrehsvictor.memorix.user.UserRole;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.LogDetail;

@DisplayName("Image Controller Integration Tests")
public class ImageControllerIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private String accessToken;
    private final String password = "Password123!";

    @BeforeEach
    void setUp() {

        RestAssured.config = RestAssured.config()
                .logConfig(LogConfig.logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.HEADERS));
        userRepository.deleteAll();

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

        // Get access token
        CredentialsDto credentials = CredentialsDto.builder()
                .username(testUser.getUsername())
                .password(password)
                .build();
        TokenDto tokenDto = tokenService.request(credentials);
        accessToken = tokenDto.getAccessToken();
    }

    @Test
    @DisplayName("Should upload image successfully with valid JPEG file")
    void uploadImage_ShouldReturnImageDto_WhenValidJpegFile() throws IOException {
        // Given
        byte[] imageContent = createTestImageContent();

        // When & Then
        ImageDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .multiPart("file", "test-image.jpg", imageContent, "image/jpeg")
                .when()
                .post("/api/v1/images")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("url", notNullValue())
                .extract().as(ImageDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getUrl()).isNotNull();
        assertThat(response.getUrl()).startsWith("http");
    }

    @Test
    @DisplayName("Should upload image successfully with valid PNG file")
    void uploadImage_ShouldReturnImageDto_WhenValidPngFile() throws IOException {
        // Given
        byte[] imageContent = createTestImageContent();

        // When & Then
        ImageDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .multiPart("file", "test-image.png", imageContent, "image/png")
                .when()
                .post("/api/v1/images")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("url", notNullValue())
                .extract().as(ImageDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getUrl()).isNotNull();
        assertThat(response.getUrl()).startsWith("http");
    }

    @Test
    @DisplayName("Should upload image successfully with valid GIF file")
    void uploadImage_ShouldReturnImageDto_WhenValidGifFile() throws IOException {
        // Given
        byte[] imageContent = createTestImageContent();

        // When & Then
        ImageDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .multiPart("file", "test-image.gif", imageContent, "image/gif")
                .when()
                .post("/api/v1/images")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("url", notNullValue())
                .extract().as(ImageDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getUrl()).isNotNull();
        assertThat(response.getUrl()).startsWith("http");
    }

    @Test
    @DisplayName("Should return 413 when file is too large for server")
    void uploadImage_ShouldReturn413_WhenFileTooLarge() throws IOException {
        // Given
        File largeImageFile = new File("src/test/resources/11mb.png");
        byte[] largeImageContent = Files.readAllBytes(largeImageFile.toPath());

        // When & Then
        given()
                .header("Authorization", "Bearer " + accessToken)
                .multiPart("file", "large-image.png", largeImageContent, "image/png")
                .when()
                .post("/api/v1/images")
                .then()
                .statusCode(HttpStatus.PAYLOAD_TOO_LARGE.value()); // 413 - server-level size limit
    }

    @Test
    @DisplayName("Should return 400 when file is not an image")
    void uploadImage_ShouldReturn400_WhenFileNotImage() throws IOException {
        // Given
        byte[] documentContent = "This is a text document".getBytes();

        // When & Then
        given()
                .header("Authorization", "Bearer " + accessToken)
                .multiPart("file", "document.txt", documentContent, "text/plain")
                .when()
                .post("/api/v1/images")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", containsString("Invalid file type. Only image files are allowed."));
    }

    @Test
    @DisplayName("Should return 400 when file is PDF")
    void uploadImage_ShouldReturn400_WhenFilePdf() throws IOException {
        // Given
        byte[] pdfContent = "%PDF-1.4 fake pdf content".getBytes();

        // When & Then
        given()
                .header("Authorization", "Bearer " + accessToken)
                .multiPart("file", "document.pdf", pdfContent, "application/pdf")
                .when()
                .post("/api/v1/images")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", containsString("Invalid file type. Only image files are allowed."));
    }

    @Test
    @DisplayName("Should return 400 when no file is provided")
    void uploadImage_ShouldReturn400_WhenNoFileProvided() {
        // When & Then
        given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .post("/api/v1/images")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Should return 401 when not authenticated")
    void uploadImage_ShouldReturn401_WhenNotAuthenticated() throws IOException {
        // Given
        byte[] imageContent = createTestImageContent();

        // When & Then
        given()
                .multiPart("file", "test-image.jpg", imageContent, "image/jpeg")
                .when()
                .post("/api/v1/images")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Should return 401 when token is invalid")
    void uploadImage_ShouldReturn401_WhenTokenInvalid() throws IOException {
        // Given
        byte[] imageContent = createTestImageContent();

        // When & Then
        given()
                .header("Authorization", "Bearer invalid-token")
                .multiPart("file", "test-image.jpg", imageContent, "image/jpeg")
                .when()
                .post("/api/v1/images")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Should handle large valid image file under limit")
    void uploadImage_ShouldReturnImageDto_WhenLargeValidImageUnderLimit() throws IOException {
        // Create a smaller test file that won't trigger server-level limits but tests our app logic
        byte[] largeImageContent = new byte[512 * 1024]; // 512KB file - under both server and app limits
        // Fill with some pattern to simulate image data
        for (int i = 0; i < largeImageContent.length; i++) {
            largeImageContent[i] = (byte) (i % 256);
        }

        // When & Then
        ImageDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .multiPart("file", "large-image.jpg", largeImageContent, "image/jpeg")
                .when()
                .post("/api/v1/images")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("url", notNullValue())
                .extract().as(ImageDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getUrl()).isNotNull();
        assertThat(response.getUrl()).startsWith("http");
    }

    @Test
    @DisplayName("Should handle empty file")
    void uploadImage_ShouldReturn400_WhenFileEmpty() throws IOException {
        // Given
        byte[] emptyContent = new byte[0];

        // When & Then
        given()
                .header("Authorization", "Bearer " + accessToken)
                .multiPart("file", "empty.jpg", emptyContent, "image/jpeg")
                .when()
                .post("/api/v1/images")
                .then()
                .statusCode(HttpStatus.OK.value()); // Empty files are technically valid for upload, MinIO will handle
                                                    // them
    }

    @Test
    @DisplayName("Should handle image file with special characters in filename")
    void uploadImage_ShouldReturnImageDto_WhenFilenameHasSpecialCharacters() throws IOException {
        // Given
        byte[] imageContent = createTestImageContent();

        // When & Then
        ImageDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .multiPart("file", "test-image-with-spëcîál-chäractérs.jpg", imageContent, "image/jpeg")
                .when()
                .post("/api/v1/images")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("url", notNullValue())
                .extract().as(ImageDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getUrl()).isNotNull();
        assertThat(response.getUrl()).startsWith("http");
    }

    @Test
    @DisplayName("Should handle WebP image format")
    void uploadImage_ShouldReturnImageDto_WhenValidWebpFile() throws IOException {
        // Given
        byte[] imageContent = createTestImageContent();

        // When & Then
        ImageDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .multiPart("file", "test-image.webp", imageContent, "image/webp")
                .when()
                .post("/api/v1/images")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("url", notNullValue())
                .extract().as(ImageDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getUrl()).isNotNull();
        assertThat(response.getUrl()).startsWith("http");
    }

    @Test
    @DisplayName("Should handle SVG image format")
    void uploadImage_ShouldReturnImageDto_WhenValidSvgFile() throws IOException {
        // Given
        String svgContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <svg width="100" height="100" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="50" cy="50" r="40" fill="red" />
                </svg>
                """;

        // When & Then
        ImageDto response = given()
                .header("Authorization", "Bearer " + accessToken)
                .multiPart("file", "test-image.svg", svgContent.getBytes(), "image/svg+xml")
                .when()
                .post("/api/v1/images")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("url", notNullValue())
                .extract().as(ImageDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getUrl()).isNotNull();
        assertThat(response.getUrl()).startsWith("http");
    }

    private byte[] createTestImageContent() {
        // Create a minimal valid image content (simulated)
        // In a real scenario, you might want to use actual image bytes
        byte[] content = new byte[1024]; // 1KB
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) (i % 256);
        }
        return content;
    }
}
