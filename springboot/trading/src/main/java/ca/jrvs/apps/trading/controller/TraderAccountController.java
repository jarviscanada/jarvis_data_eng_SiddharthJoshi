package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.config.traderaccount.SwaggerDocAddTrader;
import ca.jrvs.apps.trading.config.traderaccount.SwaggerDocDeleteTrader;
import ca.jrvs.apps.trading.config.traderaccount.SwaggerDocDepositFunds;
import ca.jrvs.apps.trading.config.traderaccount.SwaggerDocWithdrawFunds;
import ca.jrvs.apps.trading.entity.Account;
import ca.jrvs.apps.trading.entity.Trader;
import ca.jrvs.apps.trading.dto.TraderAccountView;
import ca.jrvs.apps.trading.service.TraderAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;

@RestController
@RequestMapping("/trader")
@Tag(name = "TraderAccount")
public class TraderAccountController {

    @Autowired
    private TraderAccountService traderAccountService;

    @SwaggerDocAddTrader
    @PostMapping(path = "/firstname/{firstname}/lastname/{lastname}/dob/{dob}/country/{country}/email/{email}")
    @ResponseStatus(HttpStatus.CREATED)
    public TraderAccountView createTrader(@PathVariable String firstname, @PathVariable String lastname,
                                          @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
                                          @PathVariable String country,
                                          @PathVariable String email) {

        Trader newTrader = new Trader(null, firstname, lastname,
                Date.valueOf(dob), country, email);
        return traderAccountService.createTraderAndAccount(newTrader);
    }

    @SwaggerDocAddTrader
    @PostMapping(path = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public TraderAccountView createTrader(@RequestBody Trader trader) {
        return traderAccountService.createTraderAndAccount(trader);
    }

    @SwaggerDocDeleteTrader
    @DeleteMapping(path = "{traderId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTrader(@PathVariable int traderId) {
        traderAccountService.deleteTraderById(traderId);
    }

    @SwaggerDocDepositFunds
    @PutMapping(path = "/deposit/traderId/{traderId}/amount/{amount}")
    @ResponseStatus(HttpStatus.OK)
    public Account depositFunds(@PathVariable int traderId, @PathVariable double amount) {
        return traderAccountService.depositFunds(traderId, amount);
    }

    @SwaggerDocWithdrawFunds
    @PutMapping(path = "/withdraw/traderId/{traderId}/amount/{amount}")
    @ResponseStatus(HttpStatus.OK)
    public Account withdrawFunds(@PathVariable int traderId, @PathVariable double amount) {
        return traderAccountService.withdrawFunds(traderId, amount);
    }
}
