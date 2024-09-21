package service.utils.customExceptions;

public class ServerRunException extends RuntimeException {
    public ServerRunException (String message, Throwable cause) {
        super(message, cause);
    }
}
