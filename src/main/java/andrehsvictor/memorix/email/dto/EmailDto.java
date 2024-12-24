package andrehsvictor.memorix.email.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailDto {
    private String subject;
    private String text;
    private String to;
}
