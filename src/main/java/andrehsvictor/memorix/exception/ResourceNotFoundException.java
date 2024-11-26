package andrehsvictor.memorix.exception;

public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -1251993477145024542L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
