package andrehsvictor.memorix.exception.dto;

import lombok.Data;

@Data
public class FieldErrorDto {
    private String field;
    private String message;
}
