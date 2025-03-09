package andrehsvictor.memorix.exception.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import andrehsvictor.memorix.exception.ForbiddenOperationException;
import andrehsvictor.memorix.exception.ResourceConflictException;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.exception.UnauthorizedException;
import andrehsvictor.memorix.exception.dto.ErrorsDto;
import andrehsvictor.memorix.exception.dto.FieldErrorDto;
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

    @ExceptionHandler(ResourceConflictException.class)
    public final ResponseEntity<ErrorsDto<String>> handleResourceConflictException(ResourceConflictException ex) {
        return ResponseEntity.status(409).body(ErrorsDto.of(ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public final ResponseEntity<ErrorsDto<String>> handleForbiddenOperationException(ForbiddenOperationException ex) {
        return ResponseEntity.status(403).body(ErrorsDto.of(ex.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorsDto<FieldErrorDto> errors = new ErrorsDto<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> {
            FieldErrorDto fieldError = new FieldErrorDto();
            fieldError.setField(e.getField());
            fieldError.setMessage(e.getDefaultMessage());
            errors.getErrors().add(fieldError);
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
