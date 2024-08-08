package andrehsvictor.memorix.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.entity.RefreshToken;
import andrehsvictor.memorix.entity.User;
import andrehsvictor.memorix.exception.MemorixException;
import andrehsvictor.memorix.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Value("${memorix.jwt.refresh.token.expiry:30d}")
    private Duration expiry = Duration.ofDays(30);

    private static final String REFRESH_TOKEN_NOT_FOUND = "Refresh Token not found";
    private static final String REFRESH_TOKEN_EXPIRED = "Refresh Token expired. Please sign in again";

    public RefreshToken generate(Authentication authentication) {
        String usernameOrEmail = authentication.getName();
        User user = userService.findByUsernameOrEmail(usernameOrEmail);
        if (user.getRefreshToken() != null) {
            refreshTokenRepository.delete(user.getRefreshToken());
        }
        RefreshToken refreshToken = RefreshToken.builder()
                .token(generateToken())
                .expiresAt(calculateExpiryDate())
                .user(user)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new MemorixException(HttpStatus.NOT_FOUND, REFRESH_TOKEN_NOT_FOUND));
    }

    public RefreshToken refresh(String token) {
        RefreshToken refreshToken = findByToken(token);
        validate(refreshToken);
        refreshToken.setToken(generateToken());
        refreshToken.setExpiresAt(calculateExpiryDate());
        return refreshTokenRepository.save(refreshToken);
    }

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusDays(30);
    }

    private void validate(RefreshToken refreshToken) {
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new MemorixException(HttpStatus.UNAUTHORIZED, REFRESH_TOKEN_EXPIRED);
        }
    }

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[256];
        random.nextBytes(bytes);
        return bytes.toString();
    }

}
