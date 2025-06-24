package andrehsvictor.memorix.user.it;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

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
import andrehsvictor.memorix.user.dto.MeDto;
import andrehsvictor.memorix.user.dto.UpdatePasswordDto;
import andrehsvictor.memorix.user.dto.UpdateUserDto;
import io.restassured.http.ContentType;

@DisplayName("Me Controller Integration Tests")
public class MeControllerIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private User testUser;
    private String accessToken;
    private final String initialPassword = "Password123!";

    @BeforeEach
    void setUp() {
        // Limpar usuários existentes para evitar conflitos
        userRepository.deleteAll();
        
        // Criar usuário de teste
        testUser = User.builder()
                .email("test@example.com")
                .username("testuser")
                .displayName("Test User")
                .password(passwordEncoder.encode(initialPassword))
                .role(UserRole.USER)
                .emailVerified(true)
                .build();
        
        testUser = userRepository.save(testUser);
        
        // Obter token de acesso
        CredentialsDto credentialsDto = CredentialsDto.builder()
            .username(testUser.getUsername())
            .password(initialPassword)
            .build();
        TokenDto tokenDto = tokenService.request(credentialsDto);
        accessToken = tokenDto.getAccessToken();
    }

    @Test
    @DisplayName("Should get authenticated user information")
    void getMe_ShouldReturnUserInfo_WhenAuthenticated() {
        MeDto response = given()
            .header("Authorization", "Bearer " + accessToken)
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1/users/me")
        .then()
            .statusCode(HttpStatus.OK.value())
            .extract().as(MeDto.class);
        
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testUser.getId().toString());
        assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(response.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(response.getDisplayName()).isEqualTo(testUser.getDisplayName());
    }
    
    @Test
    @DisplayName("Should return 401 when not authenticated")
    void getMe_ShouldReturn401_WhenNotAuthenticated() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1/users/me")
        .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Should update user information")
    void updateMe_ShouldUpdateUserInfo_WhenDataIsValid() {
        String newDisplayName = "Updated User";
        String newUsername = "updateduser";
        
        UpdateUserDto updateDto = UpdateUserDto.builder()
            .username(newUsername)
            .displayName(newDisplayName)
            .build();
        
        MeDto response = given()
            .header("Authorization", "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(updateDto)
        .when()
            .put("/api/v1/users/me")
        .then()
            .statusCode(HttpStatus.OK.value())
            .extract().as(MeDto.class);
        
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo(newUsername);
        assertThat(response.getDisplayName()).isEqualTo(newDisplayName);
        
        // Verificar se as alterações foram persistidas no banco de dados
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedUser.getUsername()).isEqualTo(newUsername);
        assertThat(updatedUser.getDisplayName()).isEqualTo(newDisplayName);
    }
    
    @Test
    @DisplayName("Should return 400 when update data is invalid")
    void updateMe_ShouldReturn400_WhenDataIsInvalid() {
        // Username vazio, o que deve ser inválido
        UpdateUserDto updateDto = UpdateUserDto.builder()
            .username("")
            .displayName("Valid Name")
            .build();
        
        given()
            .header("Authorization", "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(updateDto)
        .when()
            .put("/api/v1/users/me")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    @DisplayName("Should update password successfully")
    void updatePassword_ShouldUpdatePassword_WhenCredentialsAreValid() {
        String newPassword = "NewPassword123!";
        
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
            .oldPassword(initialPassword)
            .newPassword(newPassword)
            .build();
        
        given()
            .header("Authorization", "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(updatePasswordDto)
        .when()
            .put("/api/v1/users/me/password")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
        
        // Verificar se a senha foi realmente alterada no banco de dados
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(passwordEncoder.matches(newPassword, updatedUser.getPassword())).isTrue();
    }
    
    @Test
    @DisplayName("Should return 400 when old password is incorrect")
    void updatePassword_ShouldReturn400_WhenOldPasswordIsIncorrect() {
        String incorrectOldPassword = "WrongPassword123!";
        String newPassword = "NewPassword123!";
        
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
            .oldPassword(incorrectOldPassword)
            .newPassword(newPassword)
            .build();
        
        given()
            .header("Authorization", "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(updatePasswordDto)
        .when()
            .put("/api/v1/users/me/password")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    @DisplayName("Should delete user account")
    void deleteMe_ShouldDeleteUser_WhenAuthenticated() {
        given()
            .header("Authorization", "Bearer " + accessToken)
        .when()
            .delete("/api/v1/users/me")
        .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
        
        // Verificar se o usuário foi realmente excluído
        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }
}
