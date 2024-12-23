package andrehsvictor.memorix.progress;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.progress.dto.GetProgressDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProgressResource {

    private final ProgressService progressService;
    private final ProgressMapper progressMapper;

    @GetMapping("/v1/progresses")
    public Page<GetProgressDto> getAll(@AuthenticationPrincipal UUID userId, Pageable pageable) {
        Page<Progress> progresses = progressService.getAllByUserId(userId, pageable);
        Page<GetProgressDto> getProgressDtos = progresses.map(progressMapper::progressToGetProgressDto);
        return getProgressDtos;
    }
}
