package andrehsvictor.memorix.util;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FileUtil {

    public static <T> T readJson(String path, Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(new File(path), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
