package andrehsvictor.memorix.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.entity.ActivationCode;
import andrehsvictor.memorix.entity.User;
import andrehsvictor.memorix.exception.MemorixException;
import andrehsvictor.memorix.repository.ActivationCodeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivationCodeService {

    private final ActivationCodeRepository activationCodeRepository;

    private final static String ACTIVATION_CODE_NOT_FOUND = "Activation code not found";
    private final static String ACTIVATION_CODE_EXPIRED = "Activation code expired. Please request a new one";

    public ActivationCode findByCode(String code) {
        return activationCodeRepository.findByCode(code)
                .orElseThrow(() -> new MemorixException(HttpStatus.NOT_FOUND, ACTIVATION_CODE_NOT_FOUND));
    }

    public void delete(ActivationCode activationCode) {
        activationCodeRepository.delete(activationCode);
    }

    public ActivationCode create(User user) {
        deleteActivationCodeIfExists(user);
        ActivationCode activationCode = ActivationCode.builder()
                .user(user)
                .code(generateCode())
                .expiresAt(calculateExpiryDate())
                .build();
        return activationCodeRepository.save(activationCode);
    }

    public void validate(String code) {
        ActivationCode activationCode = findByCode(code);
        if (activationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new MemorixException(HttpStatus.BAD_REQUEST, ACTIVATION_CODE_EXPIRED);
        }
    }

    private void deleteActivationCodeIfExists(User user) {
        if (user.getActivationCode() != null) {
            activationCodeRepository.delete(user.getActivationCode());
        }
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusMinutes(15);
    }
}
