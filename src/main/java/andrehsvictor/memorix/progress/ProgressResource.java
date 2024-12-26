package andrehsvictor.memorix.progress;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.progress.dto.GetProgressDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProgressResource {

    private final ProgressService progressService;
    private final ProgressMapper progressMapper;

    @GetMapping("/v1/cards/{cardId}/progress")
    public GetProgressDto getByCardId(@AuthenticationPrincipal UUID userId, @PathVariable UUID cardId) {
        Progress progress = progressService.getByUserIdAndCardId(userId, cardId);
        GetProgressDto getProgressDto = progressMapper.progressToGetProgressDto(progress);
        return getProgressDto;
    }

    @PutMapping("/v1/cards/{cardId}/progress/reset")
    public ResponseEntity<GetProgressDto> reset(@AuthenticationPrincipal UUID userId, @PathVariable UUID cardId) {
        Progress progress = progressService.reset(userId, cardId);
        GetProgressDto getProgressDto = progressMapper.progressToGetProgressDto(progress);
        return ResponseEntity.ok(getProgressDto);
    }

}
