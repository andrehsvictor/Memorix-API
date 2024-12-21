package andrehsvictor.memorix.exception.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import andrehsvictor.memorix.exception.ForbiddenActionException;
import andrehsvictor.memorix.exception.MalformedRequestException;
import andrehsvictor.memorix.exception.ResourceAlreadyExistsException;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.exception.dto.ErrorDto;
import andrehsvictor.memorix.exception.dto.FieldErrorDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorDto<String>> handleAllExceptions(Exception ex) {
        log.error("An internal error occurred", ex);
        ErrorDto<String> errorDto = ErrorDto.of("An internal error occurred");
        return ResponseEntity.internalServerError().body(errorDto);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public final ResponseEntity<ErrorDto<String>> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException ex) {
        ErrorDto<String> errorDto = ErrorDto.of(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDto);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ErrorDto<String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorDto<String> errorDto = ErrorDto.of(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<FieldErrorDto> fieldErrorDto = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            FieldErrorDto error = FieldErrorDto.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
            fieldErrorDto.add(error);
        }
        ErrorDto<FieldErrorDto> errorDto = new ErrorDto<>();
        errorDto.setErrors(fieldErrorDto);
        return ResponseEntity.badRequest().body(errorDto);
    }

    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity<ErrorDto<String>> handleAuthenticationException(AuthenticationException ex) {
        ErrorDto<String> errorDto = ErrorDto.of(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDto);
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public final ResponseEntity<ErrorDto<String>> handleForbiddenActionException(ForbiddenActionException ex) {
        ErrorDto<String> errorDto = ErrorDto.of(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDto);
    }

    @ExceptionHandler(MalformedRequestException.class)
    public final ResponseEntity<ErrorDto<String>> handleMalformedRequestException(MalformedRequestException ex) {
        ErrorDto<String> errorDto = ErrorDto.of(ex.getMessage());
        return ResponseEntity.badRequest().body(errorDto);
    }
}
