package andrehsvictor.memorix.token.refreshtoken;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("refresh_token")
public class RefreshToken implements Serializable {

    private static final long serialVersionUID = -4157557238683463036L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String token;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "expires_in")
    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long expiresIn;

    public Long getExpiresIn(TimeUnit timeUnit) {
        return timeUnit.convert(expiresIn, TimeUnit.SECONDS);
    }

}
