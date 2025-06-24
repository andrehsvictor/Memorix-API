package andrehsvictor.memorix.user.it;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

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
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserRepository;
import andrehsvictor.memorix.user.UserRole;
import andrehsvictor.memorix.user.dto.CreateUserDto;
import andrehsvictor.memorix.user.dto.MeDto;
import io.restassured.http.ContentType;

@DisplayName("User Management Integration Tests")
public class UserManagementIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private User adminUser;
    private String adminAccessToken;
    private final String password = "Password123!";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = User.builder()
                .email("test@example.com")
                .username("testuser")
                .displayName("Test User")
                .password(passwordEncoder.encode(password))
                .role(UserRole.USER)
                .emailVerified(true)
                .build();
        testUser = userRepository.save(testUser);

        adminUser = User.builder()
                .email("admin@example.com")
                .username("adminuser")
                .displayName("Admin User")
                .password(passwordEncoder.encode(password))
                .role(UserRole.ADMIN)
                .emailVerified(true)
                .build();
        adminUser = userRepository.save(adminUser);

        CredentialsDto adminCredentials = CredentialsDto.builder()
                .username(adminUser.getUsername())
                .password(password)
                .build();
        TokenDto adminTokenDto = tokenService.request(adminCredentials);
        adminAccessToken = adminTokenDto.getAccessToken();
    }

    @Test
    @DisplayName("Should list users with pagination")
    void getAllUsers_ShouldReturnPagedUsers_WhenValidRequest() {
        given()
            .header("Authorization", "Bearer " + adminAccessToken)
            .contentType(ContentType.JSON)
            .queryParam("page", 0)
            .queryParam("size", 10)
        .when()
            .get("/api/v1/users")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(2));
    }

    @Test
    @DisplayName("Should filter users by username")
    void getAllUsers_ShouldFilterByUsername_WhenUsernameProvided() {
        given()
            .header("Authorization", "Bearer " + adminAccessToken)
            .contentType(ContentType.JSON)
            .queryParam("username", "adminuser")
        .when()
            .get("/api/v1/users")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("content", hasSize(1))
            .body("content[0].username", equalTo("adminuser"));
    }

    @Test
    @DisplayName("Should create a new user")
    void createUser_ShouldReturnCreatedUser_WhenValidData() {
        String newEmail = "newuser@example.com";
        String newUsername = "newuser";
        String newPassword = "NewPassword123!";
        String newDisplayName = "New User";
        
        CreateUserDto createUserDto = CreateUserDto.builder()
                .email(newEmail)
                .username(newUsername)
                .password(newPassword)
                .displayName(newDisplayName)
                .build();
        
        MeDto response = given()
            .contentType(ContentType.JSON)
            .body(createUserDto)
        .when()
            .post("/api/v1/users")
        .then()
            .statusCode(HttpStatus.OK.value())
            .extract().as(MeDto.class);
        
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(newEmail);
        assertThat(response.getUsername()).isEqualTo(newUsername);
        assertThat(response.getDisplayName()).isEqualTo(newDisplayName);
        
        User createdUser = userRepository.findByUsername(newUsername).orElse(null);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo(newEmail);
    }

    @Test
    @DisplayName("Should return 400 when creating user with existing username")
    void createUser_ShouldReturn400_WhenUsernameAlreadyExists() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .email("another@example.com")
                .username(testUser.getUsername())
                .password("ValidPassword123!")
                .displayName("Another User")
                .build();
        
        given()
            .contentType(ContentType.JSON)
            .body(createUserDto)
        .when()
            .post("/api/v1/users")
        .then()
            .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Should return 400 when creating user with existing email")
    void createUser_ShouldReturn400_WhenEmailAlreadyExists() {
        CreateUserDto createUserDto = CreateUserDto.builder()
                .email(testUser.getEmail())
                .username("validusername")
                .password("ValidPassword123!")
                .displayName("Another User")
                .build();
        
        given()
            .contentType(ContentType.JSON)
            .body(createUserDto)
        .when()
            .post("/api/v1/users")
        .then()
            .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("Should return user by id")
    void getUserById_ShouldReturnUser_WhenIdExists() {
        UUID userId = testUser.getId();
        
        given()
            .header("Authorization", "Bearer " + adminAccessToken)
            .contentType(ContentType.JSON)
            .queryParam("id", userId.toString())
        .when()
            .get("/api/v1/users/{id}", userId)
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("username", equalTo(testUser.getUsername()));
    }

    @Test
    @DisplayName("Should return 404 when user id does not exist")
    void getUserById_ShouldReturn404_WhenIdDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        
        given()
            .header("Authorization", "Bearer " + adminAccessToken)
            .contentType(ContentType.JSON)
            .queryParam("id", nonExistentId.toString())
        .when()
            .get("/api/v1/users/{id}", nonExistentId)
        .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
