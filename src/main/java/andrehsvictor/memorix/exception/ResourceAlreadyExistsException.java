package andrehsvictor.memorix.exception;

public class ResourceAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = -4392245065444782093L;

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

}
