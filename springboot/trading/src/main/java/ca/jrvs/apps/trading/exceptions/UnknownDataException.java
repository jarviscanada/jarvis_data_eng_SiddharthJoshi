package ca.jrvs.apps.trading.exceptions;

public class UnknownDataException extends RuntimeException {
    public UnknownDataException(String message) {
        super(message);
    }
}
