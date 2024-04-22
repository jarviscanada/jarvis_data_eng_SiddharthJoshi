package ca.jrvs.apps.trading.exceptions;

import ca.jrvs.apps.trading.dto.ErrorObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

/**
 * Handles all the exceptions encountered in the application
 * Due to the small and less complexity of the application, can handle all exceptions globally
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorObject> handleQuoteNotFoundException(
            ResourceNotFoundException notFoundException) {

        ErrorObject errorResponse = new ErrorObject();
        errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(notFoundException.getMessage());
        errorResponse.setTimestamp(new Date());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnknownDataException.class)
    public ResponseEntity<ErrorObject> handleUnknownDataException(
            UnknownDataException fatalException
    ) {
        ErrorObject errorResponse = new ErrorObject();
        errorResponse.setMessage(fatalException.getMessage());
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorObject> handleInvalidRequest(
            InvalidRequestException invalidRequest
    ) {
        ErrorObject errorResponse = new ErrorObject();
        errorResponse.setMessage(invalidRequest.getMessage());
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CannotPerformOperationException.class)
    public ResponseEntity<ErrorObject> handleInvalidOperations(
            CannotPerformOperationException failedOperationException
    ) {
        ErrorObject errorResponse = new ErrorObject();
        errorResponse.setMessage(failedOperationException.getMessage());
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatusCode(HttpStatus.FORBIDDEN.value());

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}
