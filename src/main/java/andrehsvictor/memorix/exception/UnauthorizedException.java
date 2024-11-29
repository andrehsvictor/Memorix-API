package andrehsvictor.memorix.exception;

import org.springframework.security.core.AuthenticationException;

public class UnauthorizedException extends AuthenticationException {

    private static final long serialVersionUID = -5989038234756256476L;

    public UnauthorizedException(String msg) {
        super(msg);
    }

}
