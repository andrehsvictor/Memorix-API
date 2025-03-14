package andrehsvictor.memorix.deck;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import andrehsvictor.memorix.account.AccountController;
import andrehsvictor.memorix.deck.dto.CreateDeckDto;
import andrehsvictor.memorix.deck.dto.DeckDto;
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

    private final AccountController accountController;

    private final DeckRepository deckRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final DeckMapper deckMapper;
    private final DeckUserService deckUserService;

    public Page<Deck> findAll(String query, String visibility, String accessLevel, Pageable pageable) {
        Long userId = jwtService.getCurrentUserId();
        AccessLevel accessLevelEnum = convertToAccessLevel(accessLevel);
        DeckVisibility visibilityEnum = convertToDeckVisibility(visibility);
        return deckRepository.findAllAccessibleByUserId(query, visibilityEnum, accessLevelEnum, userId, pageable);
    }

    public Page<Deck> findAllPublic(String query, Pageable pageable) {
        return deckRepository.findAllByVisibility(query, DeckVisibility.PUBLIC, pageable);
    }

    public Page<Deck> findAllByAuthorId(String query, Long authorId, Pageable pageable) {
        Long userId = jwtService.getCurrentUserId();
        if (authorId.equals(userId)) {
            return findAll(query, null, AccessLevel.OWNER.name(), pageable);
        }
        return deckRepository.findAllByAuthorIdAndVisibility(query, authorId, DeckVisibility.PUBLIC, pageable);
    }

    public DeckDto toDto(Deck deck) {
        return deckMapper.deckToDeckDto(deck);
    }

    @Transactional
    public Deck create(CreateDeckDto createDeckDto) {
        Deck deck = deckMapper.createDeckDtoToDeck(createDeckDto);
        deck.setAuthor(userService.findMyself());
        return deckRepository.save(deck);
    }

    public Deck findById(Long id) {
        Long userId = jwtService.getCurrentUserId();
        return deckRepository.findVisibleByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException(Deck.class, "ID", id));
    }

    @Transactional
    public void delete(Long id) {
        Long userId = jwtService.getCurrentUserId();
        Deck deck = findById(id);
        if (!isUserAuthor(userId, deck)) {
            throw new ForbiddenOperationException("You are not the author of this deck");
        }
        deckRepository.deleteById(id);
    }

    @Transactional
    public Deck update(Long id, UpdateDeckDto updateDeckDto) {
        Deck deck = findById(id);
        Long userId = jwtService.getCurrentUserId();
        if (!isUserAuthor(userId, deck) || !hasAccessLevel(userId, id, AccessLevel.EDITOR)) {
            throw new ForbiddenOperationException("You are not the author or an editor of this deck");
        }
        deckMapper.updateDeckFromUpdateDeckDto(updateDeckDto, deck);
        return deckRepository.save(deck);
    }

    @Transactional
    public void updateVisibility(Long id, String visibility) {
        Deck deck = findById(id);
        Long userId = jwtService.getCurrentUserId();
        if (!isUserAuthor(userId, deck)) {
            throw new ForbiddenOperationException("You are not the author of this deck");
        }
        deck.setVisibility(DeckVisibility.fromString(visibility));
        deckRepository.save(deck);
    }

    public void append(Long deckId) {
        Long userId = jwtService.getCurrentUserId();
        if (deckUserService.hasAccess(userId, deckId)) {
            throw new ForbiddenOperationException("You already have access to this deck");
        }
        deckUserService.create(userId, deckId, AccessLevel.VIEWER);
    }

    public void remove(Long deckId) {
        Long userId = jwtService.getCurrentUserId();
        if (!deckUserService.hasAccess(userId, deckId)) {
            throw new ForbiddenOperationException("You do not have access to this deck");
        }
        deckUserService.delete(userId, deckId);
    }

    private boolean hasAccessLevel(Long userId, Long deckId, AccessLevel accessLevel) {
        return deckUserService.hasAccessLevel(userId, deckId, accessLevel);
    }

    private boolean isUserAuthor(Long userId, Deck deck) {
        return deck.getAuthor().getId().equals(userId);
    }

    private DeckVisibility convertToDeckVisibility(String visibility) {
        return visibility != null ? DeckVisibility.fromString(visibility) : null;
    }

    private AccessLevel convertToAccessLevel(String accessLevel) {
        return accessLevel != null ? AccessLevel.fromString(accessLevel) : null;
    }

}
