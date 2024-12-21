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
    private Set<String> options;
    private Integer correctOptionIndex;
    private String createdAt;
    private String updatedAt;
}
