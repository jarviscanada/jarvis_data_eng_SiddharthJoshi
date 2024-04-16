package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.domain.Quote;
import ca.jrvs.apps.trading.dto.IexQuote;
import ca.jrvs.apps.trading.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quote")
public class QuoteController {

    @Autowired
    private QuoteService quoteService;

    @GetMapping(path = "/iex/ticker/{ticker}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<IexQuote> getQuotes(@PathVariable String ticker) {
        return quoteService.findIexQuotes(ticker);
    }

    @PutMapping(path = "/iexMarketData")
    @ResponseStatus(HttpStatus.OK)
    public void updateMarketData() {
        quoteService.updateMarketData();
    }

    @PutMapping(path = "/")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Quote putQuote(@RequestBody Quote quoteToUpdate) {
        return quoteService.updateQuote(quoteToUpdate);
    }

    @PostMapping(path = "/tickerId/{tickerId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Quote createQuote(@PathVariable String tickerId, @RequestBody Quote newQuote) {
        return quoteService.addQuote(tickerId, newQuote);
    }

    @GetMapping(path = "/dailyList")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Quote> getDailyList() {
        return quoteService.findAllQuotes();
    }

    @GetMapping(path = "tickerId/{tickerId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Quote getSpecificQuote(@PathVariable String tickerId) {
        return quoteService.findQuoteById(tickerId);
    }
}
