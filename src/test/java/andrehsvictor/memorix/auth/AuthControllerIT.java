package andrehsvictor.memorix.auth;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import andrehsvictor.memorix.AbstractIntegrationTest;
import andrehsvictor.memorix.auth.dto.CredentialsDto;
import andrehsvictor.memorix.auth.dto.IdTokenDto;
import andrehsvictor.memorix.auth.dto.RefreshTokenDto;
import andrehsvictor.memorix.auth.dto.RevokeTokenDto;
import andrehsvictor.memorix.auth.dto.TokenDto;
import andrehsvictor.memorix.common.google.GoogleAuthenticationService;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserRepository;
import andrehsvictor.memorix.user.UserRole;
import io.restassured.http.ContentType;

@DisplayName("Auth Controller Integration Tests")
public class AuthControllerIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private final String password = "Password123!";
    private final String mockIdToken = "mock-google-id-token";

    @MockitoBean
    private GoogleAuthenticationService googleAuthenticationService;

    @BeforeEach
    void setUp() {
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

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(testUser.getUsername())
                .password(password)
                .authorities(AuthorityUtils.createAuthorityList("ROLE_USER"))
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        when(googleAuthenticationService.authenticate(anyString())).thenReturn(authentication);
    }

    @Test
    @DisplayName("Should return token when credentials are valid")
    void requestToken_ShouldReturnToken_WhenCredentialsAreValid() {
        CredentialsDto credentialsDto = CredentialsDto.builder()
                .username(testUser.getUsername())
                .password(password)
                .build();

        TokenDto tokenDto = given()
                .contentType(ContentType.JSON)
                .body(credentialsDto)
                .when()
                .post("/api/v1/auth/token")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(TokenDto.class);

        assertThat(tokenDto).isNotNull();
        assertThat(tokenDto.getAccessToken()).isNotNull();
        assertThat(tokenDto.getRefreshToken()).isNotNull();
        assertThat(tokenDto.getExpiresIn()).isNotNull();
    }

    @Test
    @DisplayName("Should return 401 when credentials are invalid")
    void requestToken_ShouldReturn401_WhenCredentialsAreInvalid() {
        CredentialsDto credentialsDto = CredentialsDto.builder()
                .username(testUser.getUsername())
                .password("WrongPassword123!")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(credentialsDto)
                .when()
                .post("/api/v1/auth/token")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Should refresh token when refresh token is valid")
    void refreshToken_ShouldReturnNewToken_WhenRefreshTokenIsValid() {
        // First, get tokens
        CredentialsDto credentialsDto = CredentialsDto.builder()
                .username(testUser.getUsername())
                .password(password)
                .build();

        TokenDto initialTokenDto = given()
                .contentType(ContentType.JSON)
                .body(credentialsDto)
                .when()
                .post("/api/v1/auth/token")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(TokenDto.class);

        // Then, refresh token
        RefreshTokenDto refreshTokenDto = RefreshTokenDto.builder()
                .refreshToken(initialTokenDto.getRefreshToken())
                .build();

        TokenDto refreshedTokenDto = given()
                .contentType(ContentType.JSON)
                .body(refreshTokenDto)
                .when()
                .post("/api/v1/auth/refresh")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(TokenDto.class);

        // Validate response
        assertThat(refreshedTokenDto).isNotNull();
        assertThat(refreshedTokenDto.getAccessToken()).isNotNull();
        assertThat(refreshedTokenDto.getRefreshToken()).isNotNull();
        assertThat(refreshedTokenDto.getExpiresIn()).isNotNull();

        // Validate tokens are different
        assertThat(refreshedTokenDto.getAccessToken()).isNotEqualTo(initialTokenDto.getAccessToken());
        assertThat(refreshedTokenDto.getRefreshToken()).isNotEqualTo(initialTokenDto.getRefreshToken());

        // Validate old refresh token is revoked
        given()
                .contentType(ContentType.JSON)
                .body(RefreshTokenDto.builder()
                        .refreshToken(initialTokenDto.getRefreshToken())
                        .build())
                .post("/api/v1/auth/refresh")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Should return 401 when refresh token is invalid")
    void refreshToken_ShouldReturn401_WhenRefreshTokenIsInvalid() {
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJpbnZhbGlkIiwiaWF0IjoxNjE2MjYwMDAwfQ.invalid-signature";
        RefreshTokenDto refreshTokenDto = RefreshTokenDto.builder()
                .refreshToken(invalidToken)
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(refreshTokenDto)
                .when()
                .post("/api/v1/auth/refresh")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Should revoke token successfully")
    void revokeToken_ShouldRevokeToken_WhenTokenIsValid() {
        // First, get tokens
        CredentialsDto credentialsDto = CredentialsDto.builder()
                .username(testUser.getUsername())
                .password(password)
                .build();

        TokenDto tokenDto = given()
                .contentType(ContentType.JSON)
                .body(credentialsDto)
                .when()
                .post("/api/v1/auth/token")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(TokenDto.class);

        // Then, revoke token
        RevokeTokenDto revokeTokenDto = RevokeTokenDto.builder()
                .token(tokenDto.getAccessToken())
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(revokeTokenDto)
                .when()
                .post("/api/v1/auth/revoke")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // Try to use the revoked token in a protected endpoint
        given()
                .header("Authorization", "Bearer " + tokenDto.getAccessToken())
                .when()
                .get("/api/v1/users/me")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Should return token when Google ID token is valid")
    void googleAuth_ShouldReturnToken_WhenIdTokenIsValid() {
        IdTokenDto idTokenDto = new IdTokenDto();
        idTokenDto.setIdToken(mockIdToken);

        TokenDto tokenDto = given()
                .contentType(ContentType.JSON)
                .body(idTokenDto)
                .when()
                .post("/api/v1/auth/google")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(TokenDto.class);

        assertThat(tokenDto).isNotNull();
        assertThat(tokenDto.getAccessToken()).isNotNull();
        assertThat(tokenDto.getRefreshToken()).isNotNull();
        assertThat(tokenDto.getExpiresIn()).isNotNull();
    }
}
