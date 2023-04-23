package antifraud.business.exception;

public class IPAlreadyExistException extends RuntimeException {
    public IPAlreadyExistException(String message) {
        super(message);
    }
}
