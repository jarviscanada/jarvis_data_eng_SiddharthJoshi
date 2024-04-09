package ca.jrvs.apps.trading.marketdata;

import ca.jrvs.apps.trading.iexquote.IexQuote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarketDataDaoIntegrationTest {

    private MarketDataDao marketDataDao;

    @BeforeEach
    public void setup() {
        marketDataDao = new MarketDataDao();
    }

    @Test
    void findByIdTest() {

        String ticker = "MSFT";
        IexQuote testQuote = marketDataDao.findById(ticker).orElse(null);
        assert testQuote != null;
        assertEquals(ticker, testQuote.getSymbol());

        String invalidTicker = "NO WAY THIS IS CORRECT";
        assertTrue(marketDataDao.findById(invalidTicker).isEmpty());
    }

    @Test
    void findAllByIdTest() {

        List<String> quoteStrings = Collections.singletonList("MSFT,AAPL,GOOG");

        List<IexQuote> iexQuoteList = (List<IexQuote>) marketDataDao.findAllById(quoteStrings);
        assertEquals(3, iexQuoteList.size());
        assertEquals("MSFT", iexQuoteList.get(0).getSymbol());

        // Should result in only one company quote
        List<String> moreQuoteStrings = Collections.singletonList("XXXXXXXXXXXXXXXXX,AAPL");

        List<IexQuote> moreIexQuotes = (List<IexQuote>) marketDataDao.findAllById(moreQuoteStrings);
        assertEquals(1, moreIexQuotes.size());
        moreIexQuotes.forEach((test) -> System.out.println(test.getSymbol()));
    }
}