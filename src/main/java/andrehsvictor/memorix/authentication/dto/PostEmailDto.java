package andrehsvictor.memorix.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostEmailDto {

    @Email(message = "Invalid e-mail")
    @Size(min = 5, message = "E-mail must have at least 5 characters")
    private String email;
    
}
