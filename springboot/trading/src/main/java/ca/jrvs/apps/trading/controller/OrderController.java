package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.config.annotations.order.SwaggerDocPostOrder;
import ca.jrvs.apps.trading.dto.MarketOrderDto;
import ca.jrvs.apps.trading.entity.SecurityOrder;
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

    @SwaggerDocPostOrder
    @PostMapping("/marketOrder")
    @ResponseStatus(value = HttpStatus.CREATED)
    public SecurityOrder postMarketOrder(@RequestBody MarketOrderDto marketOrderDto) {
        return orderService.executeMarketOrder(marketOrderDto);
    }
}
