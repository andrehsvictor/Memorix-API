package andrehsvictor.memorix.deckuser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.deckuser.dto.DeckUserDto;
import andrehsvictor.memorix.deckuser.dto.UpdateAccessLevelDto;
import andrehsvictor.memorix.util.EnumUtil;
import andrehsvictor.memorix.util.StringUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DeckUserController {

    private final DeckUserService deckUserService;

    @GetMapping("/api/v1/decks/{id}/users")
    public ResponseEntity<Page<DeckUserDto>> findAllByDeckId(@PathVariable Long id,
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(required = false) String accessLevel,
            Pageable pageable) {
        query = StringUtil.normalize(query);
        AccessLevel accessLevelEnum = EnumUtil.convertStringToEnum(AccessLevel.class, accessLevel);
        Page<DeckUser> deckUsers = deckUserService.findAllByDeckId(
                query,
                id,
                accessLevelEnum,
                pageable);
        return ResponseEntity.ok(deckUsers.map(deckUserService::toDto));
    }

    @PatchMapping("/api/v1/decks/{deckId}/users/{userId}/access-level")
    public ResponseEntity<Void> updateAccessLevel(@PathVariable Long deckId,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateAccessLevelDto updateAccessLevelDto) {
        AccessLevel accessLevel = EnumUtil.convertStringToEnum(AccessLevel.class,
                updateAccessLevelDto.getAccessLevel());
        deckUserService.updateAccessLevel(userId, deckId, accessLevel);
        return ResponseEntity.noContent().build();
    }

}
