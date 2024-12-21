package andrehsvictor.memorix.progress.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetProgressDto {
    private String id;
    private String status;
    private Float easeFactor;
    private Integer repetitions;
    private Integer interval;
    private Integer reviewsCount;
    private String nextRepetition;
    private String lastStudied;
    private Integer hits;
    private Integer misses;
    private Float averageTimeToAnswer;
    private String createdAt;
    private String updatedAt;
}
