package andrehsvictor.memorix.exception.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ErrorsDto<T> {
    private List<T> errors = new ArrayList<>();

    public static ErrorsDto<String> of(String message) {
        ErrorsDto<String> errorsDto = new ErrorsDto<>();
        errorsDto.getErrors().add(message);
        return errorsDto;
    }
}
