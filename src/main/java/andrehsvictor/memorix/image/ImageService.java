package andrehsvictor.memorix.image;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import andrehsvictor.memorix.common.exception.BadRequestException;
import andrehsvictor.memorix.common.jwt.JwtService;
import andrehsvictor.memorix.common.minio.MinioService;
import andrehsvictor.memorix.image.dto.ImageDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final MinioService minioService;
    private final JwtService jwtService;
    private static final Integer MAX_FILE_SIZE = 10 * 1024 * 1024;

    public ImageDto upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File must not be null or empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds the maximum limit of 10MB");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new BadRequestException("Invalid file type. Only image files are allowed.");
        }
        String userId = jwtService.getCurrentUserUuid().toString();
        String url = minioService.upload(file, Map.of("userId", userId));
        return ImageDto.builder()
                .url(url)
                .build();
    }

}
