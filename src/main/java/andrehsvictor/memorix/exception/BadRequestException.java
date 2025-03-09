package andrehsvictor.memorix.exception;

public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = -8428452736076675657L;

    public BadRequestException(String message) {
        super(message);
    }

}
