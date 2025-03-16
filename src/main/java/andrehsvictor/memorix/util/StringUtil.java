package andrehsvictor.memorix.util;

public class StringUtil {

    public static String normalize(String string) {
        if (string == null) {
            return null;
        }
        String trimmed = string.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}
