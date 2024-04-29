package ca.jrvs.apps.trading.exceptions;

public class CannotPerformOperationException extends RuntimeException {
    public CannotPerformOperationException(String message) {
        super(message);
    }
}
