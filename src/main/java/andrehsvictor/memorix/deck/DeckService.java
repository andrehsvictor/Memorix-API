package andrehsvictor.memorix.deck;

import org.springframework.stereotype.Service;

import andrehsvictor.memorix.deck.dto.CreateDeckDto;
import andrehsvictor.memorix.deck.dto.UpdateDeckDto;
import andrehsvictor.memorix.deckuser.AccessLevel;
import andrehsvictor.memorix.deckuser.DeckUserService;
import andrehsvictor.memorix.exception.ForbiddenOperationException;
import andrehsvictor.memorix.exception.ResourceNotFoundException;
import andrehsvictor.memorix.jwt.JwtService;
import andrehsvictor.memorix.user.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final DeckMapper deckMapper;
    private final DeckUserService deckUserService;

    public Deck create(CreateDeckDto createDeckDto) {
        Deck deck = deckMapper.createDeckDtoToDeck(createDeckDto);
        deck.setAuthor(userService.findMyself());
        return deckRepository.save(deck);
    }

    public Deck findById(Long id) {
        Long userId = jwtService.getCurrentUserId();
        return deckRepository.findAccessibleByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException(Deck.class, "ID", id));
    }

    public void delete(Long id) {
        Long userId = jwtService.getCurrentUserId();
        Deck deck = findById(id);
        if (!isUserAuthor(userId, deck)) {
            throw new ForbiddenOperationException("You are not the author of this deck");
        }
        deckRepository.deleteById(userId);
    }

    public Deck update(Long id, UpdateDeckDto updateDeckDto) {
        Deck deck = findById(id);
        Long userId = jwtService.getCurrentUserId();
        if (!isUserAuthor(userId, deck) || !isDeckUserA(userId, id, AccessLevel.EDITOR)) {
            throw new ForbiddenOperationException("You are not the author or an editor of this deck");
        }
        deckMapper.updateDeckFromUpdateDeckDto(updateDeckDto, deck);
        return deckRepository.save(deck);
    }

    private boolean isDeckUserA(Long userId, Long deckId, AccessLevel accessLevel) {
        return deckUserService.existsByUserIdAndDeckIdAndAccessLevel(userId, deckId, accessLevel);
    }

    private boolean isUserAuthor(Long userId, Deck deck) {
        return deck.getAuthor().getId().equals(userId);
    }

}
