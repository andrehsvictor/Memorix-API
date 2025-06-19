package andrehsvictor.memorix.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardStatsDto {
    private Long total;
    private Long due;

    @JsonProperty("new")
    private Long newCards;

    private Long learning;
    private Long reviewed;
}
