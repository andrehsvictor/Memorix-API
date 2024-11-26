package andrehsvictor.memorix.exception.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FieldErrorDto {
    private String field;
    private String message;
}
