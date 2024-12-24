package andrehsvictor.memorix.token.actiontoken;

import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActionTokenService {

    private final ActionTokenRepository actionTokenRepository;

    @Value("${memorix.security.action-token.lifespan:1h}")
    private Duration lifespan = Duration.ofHours(1);

    public ActionToken issue(ActionType action, String email) {
        ActionToken actionToken = new ActionToken();
        actionToken.setToken(generateBase64UUID());
        actionToken.setAction(action);
        actionToken.setEmail(email);
        actionToken.setLifespan(lifespan.toMillis());
        actionTokenRepository.save(actionToken);
        return actionToken;
    }

    public boolean isValid(String token) {
        return actionTokenRepository.existsByToken(token);
    }

    public ActionToken get(String token) {
        return actionTokenRepository.findByToken(token);
    }

    public void delete(String token) {
        actionTokenRepository.deleteByToken(token);
    }

    private String generateBase64UUID() {
        return Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
    }
}
