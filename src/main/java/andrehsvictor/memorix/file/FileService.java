package andrehsvictor.memorix.file;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import andrehsvictor.memorix.exception.MalformedRequestException;
import andrehsvictor.memorix.minio.MinioService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioService minioService;

    public String read(String path) {
        try {
            Path filePath;
            if (path.startsWith("file://")) {
                filePath = Path.of(path.substring(7));
            } else if (path.startsWith("classpath:")) {
                filePath = new ClassPathResource(path.substring(10)).getFile().toPath();
            } else {
                filePath = Path.of(path);
            }
            return new String(Files.readAllBytes(filePath));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }

    public String upload(MultipartFile file) {
        if (file.getContentType() != null && !file.getContentType().startsWith("image/")) {
            throw new MalformedRequestException("Only images are allowed");
        }
        return minioService.upload(file);
    }

}
