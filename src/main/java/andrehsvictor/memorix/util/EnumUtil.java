package andrehsvictor.memorix.util;

public class EnumUtil {
    public static <E extends Enum<E>> E convertStringToEnum(Class<E> enumClass, String value) {
        if (enumClass == null) {
            return null;
        }
        if (value == null) {
            return null;
        }
        try {
            value = value.toUpperCase().trim()
                    .replace(" ", "_")
                    .replace("-", "_")
                    .replace(".", "_");
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
