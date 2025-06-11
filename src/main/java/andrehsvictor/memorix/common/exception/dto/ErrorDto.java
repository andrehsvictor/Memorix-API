package andrehsvictor.memorix.common.exception.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDto {

    private Integer status;
    private String message;
    private String timestamp;
    private String traceId;

    @Builder.Default
    private List<ValidationErrorDto> errors = new ArrayList<>();

}
