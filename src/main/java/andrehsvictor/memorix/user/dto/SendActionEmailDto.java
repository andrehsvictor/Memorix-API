package andrehsvictor.memorix.user.dto;

import java.io.Serializable;

import andrehsvictor.memorix.user.EmailAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendActionEmailDto implements Serializable {

    private static final long serialVersionUID = -2816457549944893990L;

    @NotBlank(message = "URL cannot be blank")
    @Pattern(regexp = "^(https?://)?[a-zA-Z0-9.-]+(:\\d+)?(/.*)?$", message = "Invalid URL format")
    private String url;

    @NotBlank(message = "Email cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email format")
    private String email;

    @NotNull(message = "Action is required")
    private EmailAction action;

}
