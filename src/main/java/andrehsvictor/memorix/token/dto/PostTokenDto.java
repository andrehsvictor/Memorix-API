package andrehsvictor.memorix.token.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class PostTokenDto {
    private String username;

    @ToString.Include(name = "[PROTECTED]")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

}
