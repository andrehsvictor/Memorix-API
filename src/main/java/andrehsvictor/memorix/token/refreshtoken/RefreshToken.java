package andrehsvictor.memorix.token.refreshtoken;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@RedisHash
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class RefreshToken implements Serializable {

    private static final long serialVersionUID = -4157557238683463036L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long ttl;

    public static RefreshToken of(UUID id, Long ttl) {
        return RefreshToken.builder()
                .id(id)
                .ttl(ttl)
                .build();
    }

}
