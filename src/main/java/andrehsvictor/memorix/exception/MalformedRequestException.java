package andrehsvictor.memorix.exception;

public class MalformedRequestException extends RuntimeException {

    private static final long serialVersionUID = -8428452736076675657L;

    public MalformedRequestException(String message) {
        super(message);
    }

}
