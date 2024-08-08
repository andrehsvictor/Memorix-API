package andrehsvictor.memorix.presenter;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import andrehsvictor.memorix.controller.DeckController;
import andrehsvictor.memorix.dto.ResponseBody;
import andrehsvictor.memorix.dto.response.DeckResponseDTO;
import andrehsvictor.memorix.entity.Deck;
import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
@RequiredArgsConstructor
public class DeckPresenter {

    public ResponseBody<DeckResponseDTO> present(Deck deck) {
        DeckResponseDTO responseDTO = new DeckResponseDTO(deck);

        return ResponseBody.<DeckResponseDTO>builder()
                .data(responseDTO)
                .build();
    }

    public ResponseBody<List<DeckResponseDTO>> present(Page<Deck> decks) {
        List<DeckResponseDTO> responseDTOs = decks.map((deck) -> {
            DeckResponseDTO responseDTO = new DeckResponseDTO(deck);
            responseDTO.add(linkTo(methodOn(DeckController.class).findBySlug(deck.getSlug(), null)).withSelfRel());
            return responseDTO;
        }).getContent();

        return ResponseBody.<List<DeckResponseDTO>>builder()
                .data(responseDTOs)
                .totalElements(decks.getTotalElements())
                .totalPages(decks.getTotalPages())
                .page(decks.getNumber())
                .size(decks.getSize())
                .sort(decks.getSort().toString())
                .hasNext(decks.hasNext())
                .build();
    }

}
