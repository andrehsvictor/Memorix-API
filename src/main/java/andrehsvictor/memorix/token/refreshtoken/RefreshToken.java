package andrehsvictor.memorix.token.refreshtoken;

import java.io.Serializable;
import java.util.UUID;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@RedisHash("refresh_tokens")
public class RefreshToken implements Serializable {

    private static final long serialVersionUID = -4157557238683463036L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String token;

    @TimeToLive
    private Long ttl;

    private UUID userId;

    public RefreshToken(String token, Long ttl, UUID userId) {
        this.token = token;
        this.ttl = ttl;
        this.userId = userId;
    }

}
