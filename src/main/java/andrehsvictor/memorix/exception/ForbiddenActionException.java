package andrehsvictor.memorix.exception;

public class ForbiddenActionException extends RuntimeException {

    private static final long serialVersionUID = 5740978179999165528L;

    public ForbiddenActionException(String message) {
        super(message);
    }

}
