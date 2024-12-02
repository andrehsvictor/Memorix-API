package andrehsvictor.memorix.user;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.user.dto.GetMeDto;
import andrehsvictor.memorix.user.dto.GetUserDto;
import andrehsvictor.memorix.user.dto.PostUserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserResource {

    private final UserFacade userFacade;

    @GetMapping("/users")
    public Page<GetUserDto> findAll(Pageable pageable) {
        return userFacade.findAll(pageable);
    }

    @GetMapping("/users/{username}")
    public GetUserDto findByUsername(@PathVariable String username) {
        return userFacade.findByUsername(username);
    }

    @PostMapping("/users")
    public ResponseEntity<GetMeDto> create(@RequestBody @Valid PostUserDto postUserDto) {
        GetMeDto getMeDto = userFacade.create(postUserDto);
        URI location = URI.create("/users/" + getMeDto.getUsername());
        return ResponseEntity.created(location).body(getMeDto);
    }

}
