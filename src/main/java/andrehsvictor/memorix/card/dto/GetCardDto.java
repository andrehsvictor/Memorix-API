package andrehsvictor.memorix.card.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import andrehsvictor.memorix.answer.dto.GetAnswerDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCardDto {

    private String id;
    private String question;
    private String hint;
    private String template;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String answer;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<GetAnswerDto> alternatives;

    private String createdAt;
    private String updatedAt;

}
