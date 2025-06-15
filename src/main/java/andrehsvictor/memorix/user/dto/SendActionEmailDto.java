package andrehsvictor.memorix.user.dto;

import com.google.auto.value.AutoValue.Builder;

import andrehsvictor.memorix.user.EmailAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Builder
public class SendActionEmailDto {

    @NotBlank(message = "URL cannot be blank")
    @Pattern(regexp = "^(https?://)?[a-zA-Z0-9.-]+(:\\d+)?(/.*)?$", message = "Invalid URL format")
    private String url;

    @NotBlank(message = "Email cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email format")
    private String email;

    @NotNull(message = "Action is required")
    private EmailAction action;
    
}
