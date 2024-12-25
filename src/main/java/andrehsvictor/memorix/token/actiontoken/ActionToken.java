package andrehsvictor.memorix.token.actiontoken;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RedisHash("action_token")
@EqualsAndHashCode(of = "id")
public class ActionToken implements Serializable {

    private static final long serialVersionUID = 8803182935029479370L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Indexed
    private String token;

    @Enumerated(EnumType.STRING)
    private ActionType action;

    private String email;

    @TimeToLive
    private Long lifespan;

}
