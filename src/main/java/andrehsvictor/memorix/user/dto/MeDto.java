package andrehsvictor.memorix.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeDto {
    private String id;
    private String username;
    private String displayName;
    private String email;
    private boolean emailVerified;
    private String bio;
    private String pictureUrl;
    private String provider;
    private String role;
    private String createdAt;
    private String updatedAt;
}