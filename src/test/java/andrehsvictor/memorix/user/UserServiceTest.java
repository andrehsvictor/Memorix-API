package andrehsvictor.memorix.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import andrehsvictor.memorix.common.exception.ResourceConflictException;
import andrehsvictor.memorix.common.exception.ResourceNotFoundException;
import andrehsvictor.memorix.user.dto.ChangeEmailDto;
import andrehsvictor.memorix.user.dto.CreateUserDto;
import andrehsvictor.memorix.user.dto.ResetPasswordDto;
import andrehsvictor.memorix.user.dto.SendActionEmailDto;
import andrehsvictor.memorix.user.dto.UserDto;
import andrehsvictor.memorix.user.dto.VerifyEmailDto;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private EmailVerifier emailVerifier;

    @Mock
    private EmailChanger emailChanger;

    @Mock
    private PasswordResetter passwordResetter;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private CreateUserDto createUserDto;
    private UserDto userDto;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        
        testUser = User.builder()
                .id(testUserId)
                .username("testuser")
                .displayName("Test User")
                .email("test@example.com")
                .password("encoded-password")
                .build();

        createUserDto = CreateUserDto.builder()
                .username("testuser")
                .displayName("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        userDto = new UserDto();
        userDto.setId(testUserId.toString());
        userDto.setUsername("testuser");
        userDto.setDisplayName("Test User");
    }

    @Test
    @DisplayName("Should convert user to DTO successfully")
    void toDto_ShouldReturnUserDto() {
        // Given
        when(userMapper.userToUserDto(testUser)).thenReturn(userDto);

        // When
        UserDto result = userService.toDto(testUser);

        // Then
        assertThat(result).isEqualTo(userDto);
        verify(userMapper).userToUserDto(testUser);
    }

    @Test
    @DisplayName("Should create user successfully when email and username are unique")
    void create_ShouldCreateUser_WhenEmailAndUsernameAreUnique() {
        // Given
        when(userRepository.existsByEmail(createUserDto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(createUserDto.getUsername())).thenReturn(false);
        when(userMapper.createUserDtoToUser(createUserDto)).thenReturn(testUser);
        when(passwordEncoder.encode(createUserDto.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.create(createUserDto);

        // Then
        assertThat(result).isEqualTo(testUser);
        verify(userRepository).existsByEmail(createUserDto.getEmail());
        verify(userRepository).existsByUsername(createUserDto.getUsername());
        verify(passwordEncoder).encode(createUserDto.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when email already exists")
    void create_ShouldThrowResourceConflictException_WhenEmailExists() {
        // Given
        when(userRepository.existsByEmail(createUserDto.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.create(createUserDto))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("User with email already exists");

        verify(userRepository).existsByEmail(createUserDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when username already exists")
    void create_ShouldThrowResourceConflictException_WhenUsernameExists() {
        // Given
        when(userRepository.existsByEmail(createUserDto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(createUserDto.getUsername())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.create(createUserDto))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("User with username already exists");

        verify(userRepository).existsByEmail(createUserDto.getEmail());
        verify(userRepository).existsByUsername(createUserDto.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should get all users with filters successfully")
    void getAllWithFilters_ShouldReturnPageOfUsers() {
        // Given
        String query = "test";
        String username = "testuser";
        String displayName = "Test User";
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> expectedPage = new PageImpl<>(List.of(testUser), pageable, 1);

        when(userRepository.findAllWithFilters(query, username, displayName, pageable))
                .thenReturn(expectedPage);

        // When
        Page<User> result = userService.getAllWithFilters(query, username, displayName, pageable);

        // Then
        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testUser);
    }

    @Test
    @DisplayName("Should find user by email successfully")
    void getByEmail_ShouldReturnUser_WhenUserExists() {
        // Given
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getByEmail(email);

        // Then
        assertThat(result).isEqualTo(testUser);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found by email")
    void getByEmail_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("email")
                .hasMessageContaining(email);
    }

    @Test
    @DisplayName("Should find user by provider ID successfully")
    void getByProviderId_ShouldReturnUser_WhenUserExists() {
        // Given
        String providerId = "google-123";
        when(userRepository.findByProviderId(providerId)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getByProviderId(providerId);

        // Then
        assertThat(result).isEqualTo(testUser);
        verify(userRepository).findByProviderId(providerId);
    }

    @Test
    @DisplayName("Should find user by username successfully")
    void getByUsername_ShouldReturnUser_WhenUserExists() {
        // Given
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getByUsername(username);

        // Then
        assertThat(result).isEqualTo(testUser);
        verify(userRepository).findByUsername(username);
    }

    @Test
    @DisplayName("Should find user by ID successfully")
    void getById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getById(testUserId);

        // Then
        assertThat(result).isEqualTo(testUser);
        verify(userRepository).findById(testUserId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found by ID")
    void getById_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getById(testUserId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("ID")
                .hasMessageContaining(testUserId.toString());
    }

    @Test
    @DisplayName("Should find user by email verification token successfully")
    void getByEmailVerificationToken_ShouldReturnUser_WhenTokenExists() {
        // Given
        String token = "verification-token";
        when(userRepository.findByEmailVerificationToken(token)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getByEmailVerificationToken(token);

        // Then
        assertThat(result).isEqualTo(testUser);
        verify(userRepository).findByEmailVerificationToken(token);
    }

    @Test
    @DisplayName("Should save user successfully")
    void save_ShouldReturnSavedUser() {
        // Given
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        User result = userService.save(testUser);

        // Then
        assertThat(result).isEqualTo(testUser);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should check if user exists by username")
    void existsByUsername_ShouldReturnCorrectValue() {
        // Given
        String username = "testuser";
        when(userRepository.existsByUsername(username)).thenReturn(true);

        // When
        boolean result = userService.existsByUsername(username);

        // Then
        assertThat(result).isTrue();
        verify(userRepository).existsByUsername(username);
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void existsByEmail_ShouldReturnCorrectValue() {
        // Given
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When
        boolean result = userService.existsByEmail(email);

        // Then
        assertThat(result).isTrue();
        verify(userRepository).existsByEmail(email);
    }

    @Test
    @DisplayName("Should send verify email action successfully")
    void sendActionEmail_ShouldSendVerifyEmailMessage() {
        // Given
        SendActionEmailDto dto = new SendActionEmailDto();
        dto.setAction(EmailAction.VERIFY_EMAIL);
        dto.setEmail("test@example.com");
        dto.setUrl("http://example.com");

        // When
        userService.sendActionEmail(dto);

        // Then
        verify(rabbitTemplate).convertAndSend("email-actions.v1.verify-email", dto);
    }

    @Test
    @DisplayName("Should verify email successfully")
    void verifyEmail_ShouldCallEmailVerifier() {
        // Given
        VerifyEmailDto dto = VerifyEmailDto.builder()
                .token("verification-token")
                .build();

        // When
        userService.verifyEmail(dto);

        // Then
        verify(emailVerifier).verifyEmail(dto.getToken());
    }

    @Test
    @DisplayName("Should change email successfully")
    void changeEmail_ShouldCallEmailChanger() {
        // Given
        ChangeEmailDto dto = ChangeEmailDto.builder()
                .token("change-email-token")
                .build();

        // When
        userService.changeEmail(dto);

        // Then
        verify(emailChanger).changeEmail(dto.getToken());
    }

    @Test
    @DisplayName("Should reset password successfully")
    void resetPassword_ShouldCallPasswordResetter() {
        // Given
        ResetPasswordDto dto = ResetPasswordDto.builder()
                .token("reset-token")
                .password("newpassword123")
                .build();

        // When
        userService.resetPassword(dto);

        // Then
        verify(passwordResetter).resetPassword(dto.getToken(), dto.getPassword());
    }

    @Test
    @DisplayName("Should delete user by ID successfully")
    void delete_ShouldDeleteUser() {
        // When
        userService.delete(testUserId);

        // Then
        verify(userRepository).deleteById(testUserId);
    }
}
