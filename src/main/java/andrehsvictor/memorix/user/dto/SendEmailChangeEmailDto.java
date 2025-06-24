package andrehsvictor.memorix.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendEmailChangeEmailDto {

    @JsonProperty("new")
    @NotBlank(message = "New email cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String newEmail;

    @NotBlank(message = "URL cannot be blank")
    @Size(max = 255, message = "URL cannot exceed 255 characters")
    @Pattern(regexp = "^(https?://)?[a-zA-Z0-9.-]+(:\\d+)?(/.*)?$", message = "Invalid URL format")
    private String url;
    
}
