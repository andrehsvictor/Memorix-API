package andrehsvictor.memorix.file;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FileResource {

    private final FileService fileService;

    @PostMapping("/v1/files")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Map<String, String> upload(MultipartFile file) {
        return Map.of("url", fileService.upload(file));
    }
}
