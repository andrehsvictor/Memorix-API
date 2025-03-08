package andrehsvictor.memorix.deckuser;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeckUserService {

    private final DeckUserRepository deckUserRepository;

    public boolean existsByUserIdAndDeckIdAndAccessLevel(Long userId, Long deckId, AccessLevel accessLevel) {
        return deckUserRepository.existsByUserIdAndDeckIdAndAccessLevel(userId, deckId, accessLevel);
    }

}
