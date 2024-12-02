package andrehsvictor.memorix.token.actiontoken;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActionTokenService {

    private final ActionTokenRepository actionTokenRepository;

    @Value("${memorix.security.action-token.expires-in:PT1H}")
    private Duration expiresIn = Duration.ofHours(1);

    public ActionToken issue(ActionType action, UUID userId) {
        ActionToken actionToken = ActionToken.builder()
                .action(action)
                .userId(userId)
                .expiresIn(expiresIn.getSeconds())
                .token(UUID.randomUUID().toString())
                .build();
        return actionTokenRepository.save(actionToken);
    }
}
