package ca.jrvs.apps.trading.config.annotations.dashboard;

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
        description = "Get the Trader Profile (Trader and Account Details)",
        responses = {
                @ApiResponse(
                        description = "OK",
                        responseCode = "200"
                ),
                @ApiResponse(
                        description = "Forbidden",
                        responseCode = "403",
                        content = @Content(
                                schema = @Schema(implementation = ErrorObject.class)
                        )
                ),
                @ApiResponse(
                        description = "Not Found",
                        responseCode = "404",
                        content = @Content(
                                schema = @Schema(implementation = ErrorObject.class)
                        )
                )
        }
)
public @interface SwaggerDocGetTraderProfile {

}
