package andrehsvictor.memorix.user;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.user.dto.GetMeDto;
import andrehsvictor.memorix.user.dto.GetUserDto;
import andrehsvictor.memorix.user.dto.PostUserDto;
import andrehsvictor.memorix.user.dto.PutUserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserResource {

    private final UserFacade userFacade;

    @GetMapping("/v1/users/me")
    public GetMeDto getMe(@AuthenticationPrincipal User user) {
        return userFacade.getMe(user);
    }

    @PutMapping("/v1/users/me")
    public GetMeDto updateMe(@AuthenticationPrincipal User user, @RequestBody @Valid PutUserDto putUserDto) {
        return userFacade.updateMe(user, putUserDto);
    }

    @DeleteMapping("/v1/users/me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal User user) {
        userFacade.deleteMe(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/v1/users")
    public ResponseEntity<GetMeDto> create(@RequestBody @Valid PostUserDto postUserDto) {
        GetMeDto getMeDto = userFacade.create(postUserDto);
        URI location = URI.create("/v1/users/" + getMeDto.getUsername());
        return ResponseEntity.created(location).body(getMeDto);
    }

    @GetMapping("/v1/users/{username}")
    public GetUserDto findByUsername(String username) {
        return userFacade.findByUsername(username);
    }

    @GetMapping("/v1/users")
    public Page<GetUserDto> findAll(Pageable pageable, String displayName, String username) {
        if (displayName.isBlank() && username.isBlank()) {
            return userFacade.findAll(pageable);
        }
        return userFacade.searchByDisplayNameOrUsername(displayName, username, pageable);
    }
}
