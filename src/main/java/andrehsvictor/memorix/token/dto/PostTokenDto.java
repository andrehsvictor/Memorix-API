package andrehsvictor.memorix.token.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class PostTokenDto {

    @NotBlank(message = "The username must be provided.")
    private String username;

    @ToString.Include(name = "[PROTECTED]")
    @NotBlank(message = "The password must be provided.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

}
