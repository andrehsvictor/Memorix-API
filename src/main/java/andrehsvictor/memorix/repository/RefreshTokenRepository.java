package andrehsvictor.memorix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import andrehsvictor.memorix.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByToken(String token);

}
