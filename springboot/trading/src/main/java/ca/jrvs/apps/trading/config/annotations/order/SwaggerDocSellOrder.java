package ca.jrvs.apps.trading.config.annotations.order;

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
        description = "Liquidates all the stocks of a specific ticker",
        responses = {
                @ApiResponse(
                        description = "OK",
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
                ),
                @ApiResponse(
                        description = "Resource not Found",
                        responseCode = "404",
                        content = @Content(
                                schema = @Schema(implementation = ErrorObject.class)
                        )
                )
        }
)
public @interface SwaggerDocSellOrder {

}
