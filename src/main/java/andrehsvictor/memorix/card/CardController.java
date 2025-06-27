package andrehsvictor.memorix.card;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.card.dto.CardDto;
import andrehsvictor.memorix.card.dto.CardStatsDto;
import andrehsvictor.memorix.card.dto.CreateCardDto;
import andrehsvictor.memorix.card.dto.UpdateCardDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Cards", description = "Flashcard management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CardController {

    private final CardService cardService;

    @Operation(
        summary = "Get all cards", 
        description = "Retrieve a paginated list of all cards, optionally filtered by due status"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Cards retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping("/api/v1/cards")
    public Page<CardDto> getAll(
            @Parameter(description = "Filter cards by due status - true for due cards only, false for not due, null for all") 
            @RequestParam(required = false) Boolean due,
            @Parameter(description = "Pagination parameters") 
            Pageable pageable) {
        Page<Card> cards = cardService.getAll(due, pageable);
        return cards.map(cardService::toDto);
    }

    @Operation(
        summary = "Get card statistics", 
        description = "Retrieve overall statistics for all user's cards"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Card statistics retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardStatsDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping("/api/v1/cards/stats")
    public CardStatsDto getStats() {
        return cardService.getStats();
    }

    @Operation(
        summary = "Get card statistics by deck", 
        description = "Retrieve statistics for cards in a specific deck"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Deck card statistics retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardStatsDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Deck not found")
    })
    @GetMapping("/api/v1/decks/{deckId}/cards/stats")
    public CardStatsDto getStatsByDeckId(
            @Parameter(description = "Deck unique identifier") 
            @PathVariable UUID deckId) {
        return cardService.getStatsByDeckId(deckId);
    }

    @Operation(
        summary = "Get cards by deck", 
        description = "Retrieve a paginated list of cards from a specific deck, optionally filtered by due status"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Deck cards retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Deck not found")
    })
    @GetMapping("/api/v1/decks/{deckId}/cards")
    public Page<CardDto> getAllByDeckId(
            @Parameter(description = "Deck unique identifier") 
            @PathVariable UUID deckId,
            @Parameter(description = "Filter cards by due status - true for due cards only, false for not due, null for all") 
            @RequestParam(required = false) Boolean due,
            @Parameter(description = "Pagination parameters") 
            Pageable pageable) {
        Page<Card> cards = cardService.getAllByDeckId(deckId, due, pageable);
        return cards.map(cardService::toDto);
    }

    @Operation(
        summary = "Get card by ID", 
        description = "Retrieve a specific card by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Card retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @GetMapping("/api/v1/cards/{cardId}")
    public CardDto getById(
            @Parameter(description = "Card unique identifier") 
            @PathVariable UUID cardId) {
        Card card = cardService.getById(cardId);
        return cardService.toDto(card);
    }

    @Operation(
        summary = "Create new card", 
        description = "Create a new flashcard in a specific deck"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Card created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid card data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Deck not found")
    })
    @PostMapping("/api/v1/decks/{deckId}/cards")
    public CardDto create(
            @Parameter(description = "Deck unique identifier") 
            @PathVariable UUID deckId,
            @Parameter(description = "Card creation data") 
            @Valid @RequestBody CreateCardDto createCardDto) {
        Card card = cardService.create(deckId, createCardDto);
        return cardService.toDto(card);
    }

    @Operation(
        summary = "Update card", 
        description = "Update an existing flashcard"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Card updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CardDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid card data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @PutMapping("/api/v1/cards/{cardId}")
    public CardDto update(
            @Parameter(description = "Card unique identifier") 
            @PathVariable UUID cardId,
            @Parameter(description = "Card update data") 
            @Valid @RequestBody UpdateCardDto updateCardDto) {
        Card card = cardService.update(cardId, updateCardDto);
        return cardService.toDto(card);
    }

    @Operation(
        summary = "Delete card", 
        description = "Delete a flashcard permanently"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Card deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @DeleteMapping("/api/v1/cards/{cardId}")
    public void delete(
            @Parameter(description = "Card unique identifier") 
            @PathVariable UUID cardId) {
        cardService.delete(cardId);
    }

}
