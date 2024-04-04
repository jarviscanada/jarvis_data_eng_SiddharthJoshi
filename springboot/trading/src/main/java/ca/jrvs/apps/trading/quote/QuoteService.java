package ca.jrvs.apps.trading.quote;

import ca.jrvs.apps.trading.dto.IexQuote;
import ca.jrvs.apps.trading.marketdata.MarketDataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class QuoteService {

    @Autowired
    private MarketDataDao marketDataDao;

    public List<IexQuote> findIexQuotes(String ticker) {

        // If "," exists, there is potentially a request for multiple company quotes. So in that case, call findAllById method in the repository layer
        if (ticker.contains(",")) {
            return (List<IexQuote>) marketDataDao.findAllById(Collections.singletonList(ticker));
        }

        // Otherwise, there is a request for just one company quote. So, can call findById in the repository layer
        return marketDataDao.findById(ticker).stream().toList();
    }
}
