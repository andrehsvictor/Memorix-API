package andrehsvictor.memorix.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePasswordDto {

    @JsonProperty("old")
    @NotBlank(message = "Old password cannot be blank")
    private String oldPassword;

    @JsonProperty("new")
    @NotBlank(message = "New password cannot be blank")
    @Size(min = 8, max = 100, message = "New password must be at least 8 characters long")
    private String newPassword;

}
