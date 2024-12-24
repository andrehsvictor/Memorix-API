package andrehsvictor.memorix.file;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {

    public String importFileAsText(String path) {
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
            throw new RuntimeException("Failed to import file as text", e);
        }
    }
}
