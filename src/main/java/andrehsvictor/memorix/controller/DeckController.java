package andrehsvictor.memorix.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.dto.ResponseBody;
import andrehsvictor.memorix.dto.response.DeckResponseDTO;
import andrehsvictor.memorix.entity.Deck;
import andrehsvictor.memorix.presenter.DeckPresenter;
import andrehsvictor.memorix.service.DeckService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/decks")
public class DeckController {

    private final DeckService deckService;
    private final DeckPresenter deckPresenter;

    @GetMapping("/{slug}")
    public ResponseBody<DeckResponseDTO> findBySlug(@PathVariable String slug,
            @RequestParam(defaultValue = "name,slug,description,createdAt,updatedAt,totalCards,totalCardsToReview") String include) {
        Deck deck = deckService.findBySlug(slug);
        return deckPresenter.present(deck);
    }

    @GetMapping
    public ResponseBody<List<DeckResponseDTO>> findAll(Pageable pageable) {
        return deckPresenter.present(deckService.findAll(pageable));
    }
}
