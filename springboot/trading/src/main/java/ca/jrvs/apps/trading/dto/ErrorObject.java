package ca.jrvs.apps.trading.dto;

import lombok.Data;

import java.util.Date;

/**
 * DTO for mapping error responses back to the client.
 */

// This annotation groups functionalities from multiple annotations like @Getter, @Setter, etc
@Data
public class ErrorObject {
    private Integer statusCode;
    private String message;
    private Date timestamp;
}
