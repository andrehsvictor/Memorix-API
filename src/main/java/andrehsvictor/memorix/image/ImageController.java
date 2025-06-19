package andrehsvictor.memorix.image;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import andrehsvictor.memorix.image.dto.ImageDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/api/v1/images")
    public ImageDto upload(@Valid @NotNull(message = "File must not be null") MultipartFile file) {
        return imageService.upload(file);
    }

}
