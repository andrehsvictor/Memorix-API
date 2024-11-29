package andrehsvictor.memorix.token.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import andrehsvictor.memorix.token.validation.validator.NotRevokedValidator;
import jakarta.validation.Constraint;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotRevokedValidator.class)
public @interface NotRevoked {

    String message() default "The token is revoked.";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

}
