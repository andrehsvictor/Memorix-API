package andrehsvictor.memorix.user;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.user.dto.ChangeEmailDto;
import andrehsvictor.memorix.user.dto.CreateUserDto;
import andrehsvictor.memorix.user.dto.MeDto;
import andrehsvictor.memorix.user.dto.ResetPasswordDto;
import andrehsvictor.memorix.user.dto.SendActionEmailDto;
import andrehsvictor.memorix.user.dto.SendEmailChangeEmailDto;
import andrehsvictor.memorix.user.dto.UpdatePasswordDto;
import andrehsvictor.memorix.user.dto.UpdateUserDto;
import andrehsvictor.memorix.user.dto.UserDto;
import andrehsvictor.memorix.user.dto.VerifyEmailDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management and profile endpoints")
public class UserController {

    private final UserService userService;
    private final MeService meService;

    @Operation(
        summary = "Get current user profile", 
        description = "Get the authenticated user's profile information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User profile retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MeDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/v1/users/me")
    public MeDto getMe() {
        User user = meService.getMe();
        return meService.toDto(user);
    }

    @Operation(
        summary = "Get all users", 
        description = "Retrieve a paginated list of all users with optional filters (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Users retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/v1/users")
    public Page<UserDto> getAll(
            @Parameter(description = "Search query for username or display name") 
            @RequestParam(required = false, name = "q") String query,
            @Parameter(description = "Filter by username") 
            @RequestParam(required = false) String username,
            @Parameter(description = "Filter by display name") 
            @RequestParam(required = false) String displayName,
            @Parameter(description = "Pagination parameters") 
            Pageable pageable) {
        return userService.getAllWithFilters(query, username, displayName, pageable)
                .map(userService::toDto);
    }

    @Operation(
        summary = "Create new user", 
        description = "Register a new user account"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MeDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid user data"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/api/v1/users")
    public MeDto create(
            @Parameter(description = "User registration data") 
            @Valid @RequestBody CreateUserDto dto) {
        User user = userService.create(dto);
        return meService.toDto(user);
    }

    @Operation(
        summary = "Get user by ID", 
        description = "Retrieve a specific user by their unique identifier (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/v1/users/{id}")
    public UserDto getById(
            @Parameter(description = "User unique identifier") 
            @RequestParam UUID id) {
        User user = userService.getById(id);
        return userService.toDto(user);
    }

    @Operation(
        summary = "Send action email", 
        description = "Send an action email (verification, password reset, etc.)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Email sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid email data"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/api/v1/users/send-action-email")
    public ResponseEntity<Void> sendActionEmail(
            @Parameter(description = "Action email data") 
            @Valid @RequestBody SendActionEmailDto dto) {
        userService.sendActionEmail(dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Verify email", 
        description = "Verify user's email address using verification token"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Email verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid verification token"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/api/v1/users/verify-email")
    public ResponseEntity<Void> verifyEmail(
            @Parameter(description = "Email verification data") 
            @Valid @RequestBody VerifyEmailDto dto) {
        userService.verifyEmail(dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Change email", 
        description = "Change user's email address using verification token"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Email changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid change email token"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    @PutMapping("/api/v1/users/email")
    public ResponseEntity<Void> changeEmail(
            @Parameter(description = "Email change data") 
            @Valid @RequestBody ChangeEmailDto dto) {
        userService.changeEmail(dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Reset password", 
        description = "Reset user's password using reset token"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Password reset successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid reset token or password"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/api/v1/users/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Parameter(description = "Password reset data") 
            @Valid @RequestBody ResetPasswordDto dto) {
        userService.resetPassword(dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Update password", 
        description = "Update the authenticated user's password"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Password updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid password data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/api/v1/users/me/password")
    public ResponseEntity<Void> updatePassword(
            @Parameter(description = "Password update data") 
            @Valid @RequestBody UpdatePasswordDto dto) {
        meService.updatePassword(dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Update user profile", 
        description = "Update the authenticated user's profile information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Profile updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MeDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid profile data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "409", description = "Username or email already in use")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/api/v1/users/me")
    public MeDto updateMe(
            @Parameter(description = "Profile update data") 
            @Valid @RequestBody UpdateUserDto dto) {
        User user = meService.updateMe(dto);
        return meService.toDto(user);
    }

    @Operation(
        summary = "Send email change verification", 
        description = "Send verification email for email change request"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Verification email sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid email data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/api/v1/users/me/send-email-change-verification")
    public ResponseEntity<Void> sendEmailChangeVerification(
            @Parameter(description = "Email change verification data") 
            @Valid @RequestBody SendEmailChangeEmailDto dto) {
        meService.sendEmailChangeVerification(dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Delete user account", 
        description = "Delete the authenticated user's account permanently"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/api/v1/users/me")
    public ResponseEntity<Void> deleteMe() {
        meService.deleteMe();
        return ResponseEntity.noContent().build();
    }

}
