package andrehsvictor.memorix.account.dto;

import lombok.Data;

@Data
public class AccountDto {
    private Long id;
    private String username;
    private String email;
    private String displayName;
    private String bio;
    private String pictureUrl;
    private boolean emailVerified;
    private String provider;
    private String createdAt;
    private String updatedAt;
}
