package andrehsvictor.memorix.image;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import andrehsvictor.memorix.image.dto.ImageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Images", description = "Image upload and management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "Upload image", description = "Upload an image file and get the URL for use in decks or cards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file format or size"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @PostMapping(value = "/api/v1/images")
    public ImageDto upload(
            @Parameter(description = "Image file to upload (supported formats: JPG, PNG, GIF, WebP)", required = true, schema = @Schema(type = "string", format = "binary")) @RequestParam(required = false) MultipartFile file) {

        return imageService.upload(file);
    }
}