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
        description = "Withdraw funds from an account",
        responses = {
                @ApiResponse(
                        description = "OK",
                        responseCode = "200"
                ),
                @ApiResponse(
                        description = "Cannot Withdraw. Bad Request",
                        responseCode = "400",
                        content = @Content(
                                schema = @Schema(implementation = ErrorObject.class)
                        )
                ),
                @ApiResponse(
                        description = "Cannot Withdraw. Trader and Account not found",
                        responseCode = "404",
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
public @interface SwaggerDocWithdrawFunds {

}
