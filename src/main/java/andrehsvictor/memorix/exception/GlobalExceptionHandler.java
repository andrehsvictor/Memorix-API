package andrehsvictor.memorix.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import andrehsvictor.memorix.dto.ResponseBody;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final ResponseBody<Void> handleAllExceptions(Exception ex) {
        return ResponseBody.<Void>builder()
                .errors(List.of(ex.getMessage()))
                .build();
    }

    @ExceptionHandler(MemorixException.class)
    public final ResponseEntity<ResponseBody<Void>> handleMemorixException(MemorixException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ResponseBody.<Void>builder()
                        .errors(List.of(ex.getMessage()))
                        .build());
    }
}
