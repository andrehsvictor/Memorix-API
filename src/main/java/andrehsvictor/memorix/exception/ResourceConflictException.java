package andrehsvictor.memorix.exception;

public class ResourceConflictException extends RuntimeException {

    private static final long serialVersionUID = 7527841423687126407L;

    public ResourceConflictException(String message) {
        super(message);
    }

}
