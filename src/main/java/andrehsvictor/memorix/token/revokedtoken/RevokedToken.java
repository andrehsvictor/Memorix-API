package andrehsvictor.memorix.token.revokedtoken;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.redis.core.RedisHash;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@RedisHash("revoked_tokens")
public class RevokedToken implements Serializable {

    private static final long serialVersionUID = 7340811607801323734L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private String token;

    public RevokedToken(String token) {
        this.token = token;
    }

}
