package andrehsvictor.memorix.exception;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorsDto<T> {
    private List<T> errors = new ArrayList<>();

    public static ErrorsDto<String> of(String message) {
        ErrorsDto<String> errorsDto = new ErrorsDto<>();
        errorsDto.getErrors().add(message);
        return errorsDto;
    }
}
