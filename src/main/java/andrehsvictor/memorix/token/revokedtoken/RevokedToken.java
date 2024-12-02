package andrehsvictor.memorix.token.revokedtoken;

import java.io.Serializable;
import java.util.UUID;

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
public class RevokedToken implements Serializable {

    private static final long serialVersionUID = 7340811607801323734L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @TimeToLive
    private Long ttl;

    public static RevokedToken of(UUID id, Long ttl) {
        return RevokedToken.builder()
                .id(id)
                .ttl(ttl)
                .build();
    }

}
