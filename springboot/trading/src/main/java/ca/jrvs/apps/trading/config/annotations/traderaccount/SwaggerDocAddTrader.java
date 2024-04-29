package ca.jrvs.apps.trading.config.annotations.traderaccount;

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
        description = "Create a new Trader and a new Account associated with it",
        responses = {
                @ApiResponse(
                        description = "Created",
                        responseCode = "201"
                ),
                @ApiResponse(
                        description = "Bad Request",
                        responseCode = "400",
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
public @interface SwaggerDocAddTrader {

}
