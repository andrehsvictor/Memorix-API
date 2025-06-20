package andrehsvictor.memorix.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import andrehsvictor.memorix.common.exception.BadRequestException;
import andrehsvictor.memorix.common.exception.ResourceConflictException;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.user.dto.MeDto;
import andrehsvictor.memorix.user.dto.UpdatePasswordDto;
import andrehsvictor.memorix.user.dto.UpdateUserDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("MeService Tests")
class MeServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private MeService meService;

    private UUID testUserId;
    private User testUser;
    private MeDto testMeDto;
    private UpdatePasswordDto updatePasswordDto;
    private UpdateUserDto updateUserDto;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setPictureUrl("https://example.com/old-picture.jpg");

        testMeDto = MeDto.builder()
                .id(testUserId.toString())
                .username("testuser")
                .email("test@example.com")
                .build();

        updatePasswordDto = UpdatePasswordDto.builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword")
                .build();

        updateUserDto = UpdateUserDto.builder()
                .username("newusername")
                .pictureUrl("https://example.com/new-picture.jpg")
                .build();
    }

    @Test
    @DisplayName("Should convert user to MeDto successfully")
    void toDto_ShouldReturnMeDto_WhenUserProvided() {
        // Given
        when(userMapper.userToMeDto(testUser)).thenReturn(testMeDto);

        // When
        MeDto result = meService.toDto(testUser);

        // Then
        assertThat(result).isEqualTo(testMeDto);
        verify(userMapper).userToMeDto(testUser);
    }

    @Test
    @DisplayName("Should update password successfully when old password matches")
    void updatePassword_ShouldUpdatePassword_WhenOldPasswordMatches() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(userService.getById(testUserId)).thenReturn(testUser);
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        // When
        meService.updatePassword(updatePasswordDto);

        // Then
        assertThat(testUser.getPassword()).isEqualTo("newEncodedPassword");
        verify(jwtService).getCurrentUserUuid();
        verify(userService).getById(testUserId);
        verify(passwordEncoder).matches("oldPassword", "encodedPassword");
        verify(passwordEncoder).encode("newPassword");
        verify(userService).save(testUser);
    }

    @Test
    @DisplayName("Should throw BadRequestException when old password doesn't match")
    void updatePassword_ShouldThrowBadRequestException_WhenOldPasswordDoesNotMatch() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(userService.getById(testUserId)).thenReturn(testUser);
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> meService.updatePassword(updatePasswordDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Old password is incorrect");

        verify(passwordEncoder, never()).encode(anyString());
        verify(userService, never()).save(any());
    }

    @Test
    @DisplayName("Should get current user successfully")
    void getMe_ShouldReturnUser_WhenUserExists() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(userService.getById(testUserId)).thenReturn(testUser);

        // When
        User result = meService.getMe();

        // Then
        assertThat(result).isEqualTo(testUser);
        verify(jwtService).getCurrentUserUuid();
        verify(userService).getById(testUserId);
    }

    @Test
    @DisplayName("Should update user successfully when username is available")
    void updateMe_ShouldUpdateUser_WhenUsernameAvailable() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(userService.getById(testUserId)).thenReturn(testUser);
        when(userService.existsByUsername("newusername")).thenReturn(false);

        // When
        User result = meService.updateMe(updateUserDto);

        // Then
        assertThat(result).isEqualTo(testUser);
        verify(jwtService).getCurrentUserUuid();
        verify(userService).getById(testUserId);
        verify(userService).existsByUsername("newusername");
        verify(userMapper).updateUserFromUpdateUserDto(updateUserDto, testUser);
        verify(userService).save(testUser);
        verify(rabbitTemplate).convertAndSend("minio.v1.delete.url", "https://example.com/old-picture.jpg");
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when username already exists")
    void updateMe_ShouldThrowResourceConflictException_WhenUsernameExists() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(userService.getById(testUserId)).thenReturn(testUser);
        when(userService.existsByUsername("newusername")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> meService.updateMe(updateUserDto))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessage("Username already exists: newusername");

        verify(userMapper, never()).updateUserFromUpdateUserDto(any(), any());
        verify(userService, never()).save(any());
    }

    @Test
    @DisplayName("Should update user without deleting old picture when picture doesn't change")
    void updateMe_ShouldNotDeleteOldPicture_WhenPictureUnchanged() {
        // Given
        updateUserDto.setPictureUrl("https://example.com/old-picture.jpg"); // Same as current
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(userService.getById(testUserId)).thenReturn(testUser);
        when(userService.existsByUsername("newusername")).thenReturn(false);

        // When
        meService.updateMe(updateUserDto);

        // Then
        verify(rabbitTemplate, never()).convertAndSend(eq("minio.v1.delete.url"), anyString());
    }

    @Test
    @DisplayName("Should update user without deleting old picture when old picture is null")
    void updateMe_ShouldNotDeleteOldPicture_WhenOldPictureIsNull() {
        // Given
        testUser.setPictureUrl(null);
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(userService.getById(testUserId)).thenReturn(testUser);
        when(userService.existsByUsername("newusername")).thenReturn(false);

        // When
        meService.updateMe(updateUserDto);

        // Then
        verify(rabbitTemplate, never()).convertAndSend(eq("minio.v1.delete.url"), anyString());
    }

    @Test
    @DisplayName("Should delete user and send cleanup messages")
    void deleteMe_ShouldDeleteUserAndSendMessages() {
        // Given
        when(jwtService.getCurrentUserUuid()).thenReturn(testUserId);
        when(userService.getById(testUserId)).thenReturn(testUser);

        // When
        meService.deleteMe();

        // Then
        verify(jwtService).getCurrentUserUuid();
        verify(userService).getById(testUserId);
        verify(userService).delete(testUserId);
        verify(rabbitTemplate).convertAndSend(
                eq("minio.v1.delete.metadata"),
                eq(Map.of("userId", testUserId.toString())));
        verify(rabbitTemplate).convertAndSend("users.v1.delete", testUserId);
    }
}
