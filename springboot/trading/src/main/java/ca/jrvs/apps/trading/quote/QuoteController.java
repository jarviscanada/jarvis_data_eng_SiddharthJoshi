package ca.jrvs.apps.trading.quote;

import ca.jrvs.apps.trading.dto.IexQuote;
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
}
