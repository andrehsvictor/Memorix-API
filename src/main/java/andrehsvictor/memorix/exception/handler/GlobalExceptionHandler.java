package andrehsvictor.memorix.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import andrehsvictor.memorix.exception.ErrorsDto;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorsDto<String>> handleAllExceptions(Exception ex) {
        log.error("An error occurred", ex);
        return ResponseEntity.internalServerError().body(ErrorsDto.of("An error occurred"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ErrorsDto<String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(404).body(ErrorsDto.of(ex.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity<ErrorsDto<String>> handleUnauthorizedException(UnauthorizedException ex) {
        return ResponseEntity.status(401).body(ErrorsDto.of(ex.getMessage()));
    }
}
