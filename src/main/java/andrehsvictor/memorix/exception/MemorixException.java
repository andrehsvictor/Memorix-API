package andrehsvictor.memorix.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemorixException extends RuntimeException {

    private final HttpStatus httpStatus;

    public MemorixException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
