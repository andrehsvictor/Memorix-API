package andrehsvictor.memorix.exception;

public class ForbiddenOperationException extends RuntimeException {

    private static final long serialVersionUID = 8031306129578945291L;

    public ForbiddenOperationException(String message) {
        super(message);
    }

}
