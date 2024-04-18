package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.domain.Account;
import ca.jrvs.apps.trading.domain.Trader;
import ca.jrvs.apps.trading.dto.TraderAccountView;
import ca.jrvs.apps.trading.service.TraderAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;

@RestController
@RequestMapping("/trader")
public class TraderAccountController {

    @Autowired
    private TraderAccountService traderAccountService;

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

    @PostMapping(path = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public TraderAccountView createTrader(@RequestBody Trader trader) {
        return traderAccountService.createTraderAndAccount(trader);
    }

    @DeleteMapping(path = "{traderId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTrader(@PathVariable int traderId) {
        traderAccountService.deleteTraderById(traderId);
    }

    @PutMapping(path = "/deposit/traderId/{traderId}/amount/{amount}")
    @ResponseStatus(HttpStatus.OK)
    public Account depositFunds(@PathVariable int traderId, @PathVariable double amount) {
        return traderAccountService.depositFunds(traderId, amount);
    }

    @PutMapping(path = "/withdraw/traderId/{traderId}/amount/{amount}")
    @ResponseStatus(HttpStatus.OK)
    public Account withdrawFunds(@PathVariable int traderId, @PathVariable double amount) {
        return traderAccountService.withdrawFunds(traderId, amount);
    }
}
