package andrehsvictor.memorix.deck;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import andrehsvictor.memorix.deck.dto.CreateDeckDto;
import andrehsvictor.memorix.deck.dto.DeckDto;
import andrehsvictor.memorix.deck.dto.UpdateDeckDto;
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
@Tag(name = "Decks", description = "Deck management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class DeckController {

    private final DeckService deckService;

    @Operation(
        summary = "Get all decks", 
        description = "Retrieve a paginated list of all user's decks with optional filters"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Decks retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping("/api/v1/decks")
    public Page<DeckDto> getAll(
            @Parameter(description = "Search query for deck name or description") 
            @RequestParam(required = false, name = "q") String query,
            @Parameter(description = "Filter by exact deck name") 
            @RequestParam(required = false) String name,
            @Parameter(description = "Filter by deck description") 
            @RequestParam(required = false) String description,
            @Parameter(description = "Include only decks with cover images") 
            @RequestParam(required = false) Boolean includeWithCoverImage,
            @Parameter(description = "Include empty decks (with no cards)") 
            @RequestParam(required = false) Boolean includeEmpty,
            @Parameter(description = "Pagination parameters") 
            Pageable pageable) {
        return deckService.getAllWithFilters(
                query,
                name,
                description,
                includeWithCoverImage,
                includeEmpty,
                pageable)
                .map(deckService::toDto);
    }

    @Operation(
        summary = "Get deck by ID", 
        description = "Retrieve a specific deck by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Deck retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeckDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Deck not found")
    })
    @GetMapping("/api/v1/decks/{id}")
    public DeckDto getById(
            @Parameter(description = "Deck unique identifier") 
            @PathVariable UUID id) {
        return deckService.toDto(deckService.getById(id));
    }

    @Operation(
        summary = "Create new deck", 
        description = "Create a new deck for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Deck created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeckDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid deck data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @PostMapping("/api/v1/decks")
    public DeckDto create(
            @Parameter(description = "Deck creation data") 
            @Valid @RequestBody CreateDeckDto createDeckDto) {
        Deck deck = deckService.create(createDeckDto);
        return deckService.toDto(deck);
    }

    @Operation(
        summary = "Update deck", 
        description = "Update an existing deck"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Deck updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeckDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid deck data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Deck not found")
    })
    @PutMapping("/api/v1/decks/{id}")
    public DeckDto update(
            @Parameter(description = "Deck unique identifier") 
            @PathVariable UUID id, 
            @Parameter(description = "Deck update data") 
            @Valid @RequestBody UpdateDeckDto updateDeckDto) {
        Deck deck = deckService.update(id, updateDeckDto);
        return deckService.toDto(deck);
    }

    @Operation(
        summary = "Delete deck", 
        description = "Delete a deck permanently"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Deck deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Deck not found")
    })
    @DeleteMapping("/api/v1/decks/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Deck unique identifier") 
            @PathVariable UUID id) {
        deckService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
