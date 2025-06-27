package andrehsvictor.memorix.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.auth.dto.CredentialsDto;
import andrehsvictor.memorix.auth.dto.IdTokenDto;
import andrehsvictor.memorix.auth.dto.RefreshTokenDto;
import andrehsvictor.memorix.auth.dto.RevokeTokenDto;
import andrehsvictor.memorix.auth.dto.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and token management endpoints")
public class AuthController {

    private final TokenService tokenService;

    @Operation(
        summary = "Authenticate user", 
        description = "Authenticate user with email/username and password to receive JWT tokens"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Authentication successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid credentials format"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "403", description = "Account not verified or suspended")
    })
    @PostMapping("/api/v1/auth/token")
    public TokenDto requestToken(@Valid @RequestBody CredentialsDto credentialsDto) {
        return tokenService.request(credentialsDto);
    }

    @Operation(
        summary = "Google OAuth authentication", 
        description = "Authenticate user with Google ID token to receive JWT tokens"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Google authentication successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid Google ID token format"),
        @ApiResponse(responseCode = "401", description = "Invalid Google ID token"),
        @ApiResponse(responseCode = "403", description = "Google account not allowed")
    })
    @PostMapping("/api/v1/auth/google")
    public TokenDto google(@Valid @RequestBody IdTokenDto idTokenDto) {
        return tokenService.google(idTokenDto);
    }

    @Operation(
        summary = "Refresh JWT tokens", 
        description = "Use refresh token to obtain new access and refresh tokens"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Token refresh successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid refresh token format"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/api/v1/auth/refresh")
    public TokenDto refreshToken(@Valid @RequestBody RefreshTokenDto refreshTokenDto) {
        return tokenService.refresh(refreshTokenDto);
    }

    @Operation(
        summary = "Revoke refresh token", 
        description = "Revoke a refresh token to prevent future use"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Token revoked successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid token format"),
        @ApiResponse(responseCode = "404", description = "Token not found")
    })
    @PostMapping("/api/v1/auth/revoke")
    public ResponseEntity<Void> revokeToken(@Valid @RequestBody RevokeTokenDto revokeTokenDto) {
        tokenService.revoke(revokeTokenDto);
        return ResponseEntity.noContent().build();
    }

}
