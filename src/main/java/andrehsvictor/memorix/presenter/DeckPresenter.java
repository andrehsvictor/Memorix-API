package andrehsvictor.memorix.presenter;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import andrehsvictor.memorix.controller.DeckController;
import andrehsvictor.memorix.dto.ResponseBody;
import andrehsvictor.memorix.dto.response.DeckResponseDTO;
import andrehsvictor.memorix.entity.Deck;
import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
@RequiredArgsConstructor
public class DeckPresenter {

    public MappingJacksonValue present(Deck deck, String fields) {
        DeckResponseDTO responseDTO = convertToDTO(deck);
        ResponseBody<DeckResponseDTO> responseBody = createResponseBody(responseDTO);

        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(responseBody);
        mappingJacksonValue.setFilters(createFilter(fields));
        return mappingJacksonValue;
    }

    public MappingJacksonValue present(Page<Deck> decks, String fields) {
        List<DeckResponseDTO> responseDTOs = decks.map(this::convertToDTO).getContent();

        ResponseBody<List<DeckResponseDTO>> responseBody = createResponseBody(decks, responseDTOs);

        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(responseBody);
        mappingJacksonValue.setFilters(createFilter(fields));
        return mappingJacksonValue;
    }

    private DeckResponseDTO convertToDTO(Deck deck) {
        DeckResponseDTO responseDTO = new DeckResponseDTO(deck);
        responseDTO.add(linkTo(methodOn(DeckController.class).findBySlug(deck.getSlug(), "*"))
                .withSelfRel());
        return responseDTO;
    }

    private ResponseBody<DeckResponseDTO> createResponseBody(DeckResponseDTO responseDTO) {
        return ResponseBody.<DeckResponseDTO>builder()
                .data(responseDTO)
                .build();
    }

    private ResponseBody<List<DeckResponseDTO>> createResponseBody(Page<Deck> decks,
            List<DeckResponseDTO> responseDTOs) {
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

    private SimpleFilterProvider createFilter(String fields) {
        SimpleBeanPropertyFilter filter = (fields == null || fields.equals("*"))
                ? SimpleBeanPropertyFilter.serializeAll()
                : SimpleBeanPropertyFilter.filterOutAllExcept(fields.split(","));
        return new SimpleFilterProvider().addFilter("DeckResponseDTO", filter);
    }

}
