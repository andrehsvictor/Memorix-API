package andrehsvictor.memorix.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseBody<T> {
    private T data;
    private Integer page;
    private Integer size;
    private String sort;
    private Long totalElements;
    private Integer totalPages;
    private Boolean hasNext;

    @Builder.Default
    private List<String> errors = new ArrayList<>();
}