package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.config.annotations.order.SwaggerDocPostBuyOrder;
import ca.jrvs.apps.trading.config.annotations.order.SwaggerDocSellOrder;
import ca.jrvs.apps.trading.dto.MarketOrderBuyDto;
import ca.jrvs.apps.trading.dto.MarketOrderSellDto;
import ca.jrvs.apps.trading.dto.SellResponseDto;
import ca.jrvs.apps.trading.entity.SecurityOrder;
import ca.jrvs.apps.trading.exceptions.InvalidRequestException;
import ca.jrvs.apps.trading.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Tag(name = "Order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @SwaggerDocPostBuyOrder
    @PostMapping("/buy/marketOrder")
    @ResponseStatus(value = HttpStatus.CREATED)
    public SecurityOrder postBuyMarketOrder(@RequestBody MarketOrderBuyDto marketOrderBuyDto) {
        return orderService.executeBuyMarketOrder(marketOrderBuyDto);
    }

    @SwaggerDocSellOrder
    @PutMapping("/sell/ticker/{ticker}")
    @ResponseStatus(value = HttpStatus.OK)
    public SellResponseDto putSellMarketOrder(@PathVariable String ticker, @RequestBody MarketOrderSellDto sellOrder) {

        if (!ticker.equalsIgnoreCase(sellOrder.getTicker())) {
            throw new InvalidRequestException("Please make sure ticker in the request and in the request body matches.");
        }
        return orderService.executeSellMarketOrder(sellOrder);
    }
}
