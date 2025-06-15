package andrehsvictor.memorix.common.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileUtil {

    private final ResourceLoader resourceLoader;

    public String readFileAsString(String path) {
        try {
            Resource resource = resourceLoader.getResource(path);
            if (!resource.exists()) {
                throw new RuntimeException("File not found: " + path);
            }

            try (InputStream inputStream = resource.getInputStream()) {
                byte[] bytes = inputStream.readAllBytes();
                return new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading file: " + path, e);
        }
    }

    public String processTemplate(String path, Map<String, String> placeholders) {
        String template = readFileAsString(path);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            template = template.replace(placeholder, entry.getValue() != null ? entry.getValue() : "");
        }
        return template;
    }
}
