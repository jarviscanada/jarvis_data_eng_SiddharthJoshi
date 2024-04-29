package ca.jrvs.apps.trading.config.annotations.quote;

import ca.jrvs.apps.trading.dto.ErrorObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        description = "Update all existing Quote in the system from IEX",
        responses = {
                @ApiResponse(
                        description = "Created",
                        responseCode = "201"
                ),
                @ApiResponse(
                        description = "Issue with the server",
                        responseCode = "503",
                        content = @Content(
                                schema = @Schema(implementation = ErrorObject.class)
                        )
                ),
                @ApiResponse(
                        description = "Forbidden",
                        responseCode = "403",
                        content = @Content(
                                schema = @Schema(implementation = ErrorObject.class)
                        )
                )
        }
)
public @interface SwaggerDocUpdateAllQuotes {

}
