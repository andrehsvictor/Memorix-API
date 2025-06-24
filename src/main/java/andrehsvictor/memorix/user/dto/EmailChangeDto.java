package andrehsvictor.memorix.user.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailChangeDto implements Serializable {

    private static final long serialVersionUID = -366544392443001601L;

    private String email;
    private UUID userId;
    private String url;

}
