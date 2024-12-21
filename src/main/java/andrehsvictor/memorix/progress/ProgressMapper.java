package andrehsvictor.memorix.progress;

import org.mapstruct.Mapper;

import andrehsvictor.memorix.progress.dto.GetProgressDto;

@Mapper(componentModel = "spring")
public interface ProgressMapper {

    GetProgressDto progressToGetProgressDto(Progress progress);
    
}
