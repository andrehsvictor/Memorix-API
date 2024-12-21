package andrehsvictor.memorix.card.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import andrehsvictor.memorix.deck.dto.GetDeckDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetCardDto {
    private String id;
    private String type;
    private GetDeckDto deck;
    private String question;
    private Object answer;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<String> alternatives;
    
    private Integer answerIndex;
    private String createdAt;
    private String updatedAt;
}
