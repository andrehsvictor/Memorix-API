package andrehsvictor.memorix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import andrehsvictor.memorix.entity.ActivationCode;

public interface ActivationCodeRepository extends JpaRepository<ActivationCode, Long> {
    Optional<ActivationCode> findByCode(String code);    
}
