package antifraud.business.exception;

public class RoleConflictException extends RuntimeException {
    public RoleConflictException(String message) {
        super(message);
    }
}
