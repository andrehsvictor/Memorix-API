package andrehsvictor.memorix.deckuser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import andrehsvictor.memorix.deck.Deck;
import andrehsvictor.memorix.deck.DeckService;
import andrehsvictor.memorix.deckuser.dto.DeckUserDto;
import andrehsvictor.memorix.exception.ForbiddenOperationException;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.jwt.JwtService;
import andrehsvictor.memorix.user.User;
import andrehsvictor.memorix.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeckUserService {

    private final DeckUserRepository deckUserRepository;
    private final UserService userService;
    private final DeckService deckService;
    private final JwtService jwtService;
    private final DeckUserMapper deckUserMapper;

    public DeckUser findByUserIdAndDeckId(Long userId, Long deckId) {
        return deckUserRepository.findByUserIdAndDeckId(userId, deckId)
                .orElseThrow(() -> new ResourceNotFoundException("User has no access to this deck"));
    }

    public boolean hasAccessLevel(Long userId, Long deckId, AccessLevel accessLevel) {
        return deckUserRepository.existsByUserIdAndDeckIdAndAccessLevel(userId, deckId, accessLevel);
    }

    public boolean hasAccess(Long userId, Long deckId) {
        return deckUserRepository.existsByUserIdAndDeckId(userId, deckId);
    }

    @Transactional
    public DeckUser create(Long userId, Long deckId, AccessLevel accessLevel) {
        DeckUser deckUser = new DeckUser();
        User user = userService.findById(userId);
        Deck deck = deckService.findById(deckId);
        deckUser.setUser(user);
        deckUser.setDeck(deck);
        deckUser.setAccessLevel(accessLevel);
        return deckUserRepository.save(deckUser);
    }

    @Transactional
    public void delete(Long userId, Long deckId) {
        deckUserRepository.deleteByUserIdAndDeckId(userId, deckId);
    }

    public Page<DeckUser> findAllByDeckId(String query, Long deckId, String accessLevel, Pageable pageable) {
        AccessLevel accessLevelEnum = convertToAccessLevel(accessLevel);
        Deck deck = deckService.findById(deckId);
        Long userId = jwtService.getCurrentUserId();
        if (!isUserAuthor(userId, deck)) {
            throw new ForbiddenOperationException("You are not allowed to access users of this deck");
        }
        return deckUserRepository.findAllByDeckIdAndAccessLevel(query, deckId, accessLevelEnum, pageable);
    }

    @Transactional
    public void updateAccessLevel(Long userId, Long deckId, String accessLevel) {
        AccessLevel accessLevelEnum = convertToAccessLevel(accessLevel);
        boolean isOwnerAccessLevel = accessLevelEnum != null && accessLevelEnum.equals(AccessLevel.OWNER);
        if (isOwnerAccessLevel) {
            throw new ForbiddenOperationException("Decks can only have one owner");
        }
        Deck deck = deckService.findById(deckId);
        Long currentUserId = jwtService.getCurrentUserId();
        if (!isUserAuthor(currentUserId, deck)) {
            throw new ForbiddenOperationException("You are not allowed to update access level of this deck");
        }
        DeckUser deckUser = findByUserIdAndDeckId(userId, deckId);
        deckUser.setAccessLevel(accessLevelEnum);
        deckUserRepository.save(deckUser);
    }

    @Transactional
    public void removeAccessFromAllByDeckIdAndAccessLevel(Long deckId, AccessLevel accessLevel) {
        deckUserRepository.deleteAllByDeckIdAndAccessLevel(deckId, accessLevel);
    }

    public DeckUserDto toDto(DeckUser deckUser) {
        return deckUserMapper.deckUserToDeckUserDto(deckUser);
    }

    private boolean isUserAuthor(Long userId, Deck deck) {
        return deck.getAuthor().getId().equals(userId);
    }

    private AccessLevel convertToAccessLevel(String accessLevel) {
        if (accessLevel == null) {
            return null;
        }
        return AccessLevel.valueOf(accessLevel.toUpperCase().trim().replace(" ", "_"));
    }

}
