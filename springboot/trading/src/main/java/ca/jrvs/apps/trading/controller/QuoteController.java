package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.config.annotations.quote.*;
import ca.jrvs.apps.trading.entity.Quote;
import ca.jrvs.apps.trading.dto.IexQuote;
import ca.jrvs.apps.trading.service.QuoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quote")
@Tag(name = "Quote")
public class QuoteController {

    @Autowired
    private QuoteService quoteService;

    @SwaggerDocGetIex
    @GetMapping(path = "/iex/ticker/{ticker}")
    @ResponseStatus(HttpStatus.OK)
    public List<IexQuote> getIexQuotes(@PathVariable String ticker) {
        return quoteService.findIexQuotes(ticker);
    }

    @SwaggerDocUpdateAllQuotes
    @PutMapping(path = "/iexMarketData")
    @ResponseStatus(HttpStatus.OK)
    public void updateMarketData() {
        quoteService.updateMarketData();
    }

    @SwaggerDocUpdateQuote
    @PutMapping(path = "/")
    @ResponseStatus(HttpStatus.OK)
    public Quote putQuote(@RequestBody Quote quoteToUpdate) {
        return quoteService.updateQuote(quoteToUpdate);
    }

    @SwaggerDocAddNewQuote
    @PostMapping(path = "/tickerId/{tickerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Quote createQuote(@PathVariable String tickerId, @RequestBody Quote newQuote) {
        return quoteService.addQuote(tickerId, newQuote);
    }

    @SwaggerDocGetDailyList
    @GetMapping(path = "/dailyList")
    @ResponseStatus(HttpStatus.OK)
    public List<Quote> getDailyList() {
        return quoteService.findAllQuotes();
    }

    @SwaggerDocGetQuote
    @GetMapping(path = "tickerId/{tickerId}")
    @ResponseStatus(HttpStatus.OK)
    public Quote getSpecificQuote(@PathVariable String tickerId) {
        return quoteService.findQuoteById(tickerId);
    }
}
