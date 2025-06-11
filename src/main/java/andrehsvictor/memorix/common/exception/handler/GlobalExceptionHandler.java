package andrehsvictor.memorix.common.exception.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import andrehsvictor.memorix.common.exception.BadRequestException;
import andrehsvictor.memorix.common.exception.ForbiddenOperationException;
import andrehsvictor.memorix.common.exception.ResourceConflictException;
import andrehsvictor.memorix.common.exception.ResourceNotFoundException;
import andrehsvictor.memorix.common.exception.UnauthorizedException;
import andrehsvictor.memorix.common.exception.dto.ErrorDto;
import andrehsvictor.memorix.common.exception.dto.ValidationErrorDto;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Tracer tracer;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<ValidationErrorDto> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> ValidationErrorDto.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
                .collect(Collectors.toList());

        ErrorDto errorDto = createErrorDto(HttpStatus.BAD_REQUEST, "Validation failed");
        errorDto.setErrors(validationErrors);

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorDto errorDto = createErrorDto(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "File upload size exceeded the maximum allowed");
        return new ResponseEntity<>(errorDto, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorDto errorDto = createErrorDto(
                HttpStatus.BAD_REQUEST,
                "Missing required parameter: " + ex.getParameterName());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDto> handleBadRequestException(BadRequestException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorDto> handleForbiddenOperationException(ForbiddenOperationException ex) {
        return createErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorDto> handleResourceConflictException(ResourceConflictException ex) {
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorDto> handleUnauthorizedException(UnauthorizedException ex) {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({ BadCredentialsException.class, AuthenticationException.class })
    public ResponseEntity<ErrorDto> handleAuthenticationException(Exception ex) {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication failed");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");
    }

    private ResponseEntity<ErrorDto> createErrorResponse(HttpStatus status, String message) {
        ErrorDto errorDto = createErrorDto(status, message);
        return new ResponseEntity<>(errorDto, status);
    }

    private ErrorDto createErrorDto(HttpStatus status, String message) {
        String traceId = null;
        if (tracer.currentSpan() != null && tracer.currentSpan().context() != null) {
            traceId = tracer.currentSpan().context().traceId();
        }

        return ErrorDto.builder()
                .status(status.value())
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .traceId(traceId)
                .build();
    }
}