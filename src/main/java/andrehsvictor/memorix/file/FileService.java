package andrehsvictor.memorix.file;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {

    public String read(String path) {
        try {
            Path filePath;
            if (path.startsWith("classpath:")) {
                filePath = new ClassPathResource(path.substring(10)).getFile().toPath();
            } else {
                filePath = Path.of(path);
            }
            return new String(Files.readAllBytes(filePath));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }
}
