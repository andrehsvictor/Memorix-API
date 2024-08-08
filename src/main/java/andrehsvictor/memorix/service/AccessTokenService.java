package andrehsvictor.memorix.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class AccessTokenService {

    @Value("${memorix.jwt.access.token.expiry:15m}")
    @NotNull(message = "Access token expiry must be provided")
    private Duration expiry = Duration.ofMinutes(15);

    private final JwtEncoder jwtEncoder;

    public Jwt generate(Authentication authentication) {
        String sub = authentication.getName();
        Instant iat = Instant.now();
        Instant exp = iat.plus(expiry);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(sub)
                .issuedAt(iat)
                .expiresAt(exp)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }
}
