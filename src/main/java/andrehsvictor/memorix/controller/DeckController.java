package andrehsvictor.memorix.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public MappingJacksonValue findBySlug(@PathVariable String slug, @RequestParam(defaultValue = "*") String include) {
        Deck deck = deckService.findBySlug(slug);
        return deckPresenter.present(deck, include);
    }

    @GetMapping
    public MappingJacksonValue findAll(Pageable pageable, @RequestParam(defaultValue = "*") String include) {
        Page<Deck> decks = deckService.findAll(pageable);
        return deckPresenter.present(decks, include);
    }
}
