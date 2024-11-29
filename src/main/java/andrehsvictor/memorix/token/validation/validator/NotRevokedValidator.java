package andrehsvictor.memorix.token.validation.validator;

import org.springframework.stereotype.Component;

import andrehsvictor.memorix.exception.UnauthorizedException;
import andrehsvictor.memorix.token.revokedtoken.RevokedTokenService;
import andrehsvictor.memorix.token.validation.NotRevoked;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotRevokedValidator implements ConstraintValidator<NotRevoked, String> {

    private final RevokedTokenService revokedTokenService;

    @Override
    public boolean isValid(String token, ConstraintValidatorContext context) {
        if (token == null) {
            return true;
        }
        if (revokedTokenService.isRevoked(token)) {
            throw new UnauthorizedException("The token is revoked.");
        }
        return true;
    }

}
