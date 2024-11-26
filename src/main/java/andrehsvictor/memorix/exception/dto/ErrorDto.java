package andrehsvictor.memorix.exception.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorDto<T> {
    private List<T> errors = new ArrayList<>();

    public static <T> ErrorDto<T> of(T error) {
        ErrorDto<T> errorDto = new ErrorDto<>();
        errorDto.getErrors().add(error);
        return errorDto;
    }
}
