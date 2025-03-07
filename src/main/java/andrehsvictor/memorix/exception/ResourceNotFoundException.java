package andrehsvictor.memorix.exception;

public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -1251993477145024542L;

    public ResourceNotFoundException(Class<?> clazz, String key, Object value) {
        super(String.format("%s not found with '%s': %s", clazz.getSimpleName(), key, value));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
