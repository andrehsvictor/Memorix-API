package andrehsvictor.memorix.deckuser;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeckUserService {

    private final DeckUserRepository deckUserRepository;

    @Transactional
    public void deleteAllByDeckIdExceptOwner(UUID deckId) {
        deckUserRepository.deleteAllByDeckIdExceptOwner(deckId);
    }

    public void deleteByDeckIdAndUserId(UUID deckId, UUID userId) {
        deckUserRepository.deleteByDeckIdAndUserId(deckId, userId);
    }

    public DeckUser save(DeckUser deckUser) {
        return deckUserRepository.save(deckUser);
    }

    public boolean existsByDeckIdAndUserId(UUID deckId, UUID userId) {
        return deckUserRepository.existsByDeckIdAndUserId(deckId, userId);
    }

}