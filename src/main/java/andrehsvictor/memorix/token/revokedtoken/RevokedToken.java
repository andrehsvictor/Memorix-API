package andrehsvictor.memorix.token.revokedtoken;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

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
public class RevokedToken implements Serializable {

    private static final long serialVersionUID = 7340811607801323734L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Indexed
    private String token;

    @TimeToLive
    private Long timeToLive;

    public static RevokedToken of(String token, Long timeToLive) {
        return RevokedToken.builder()
                .token(token)
                .build();
    }

}
