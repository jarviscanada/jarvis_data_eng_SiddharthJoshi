package ca.jrvs.apps.trading.marketdata;

import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dto.IexQuote;
import ca.jrvs.apps.trading.repository.QuoteDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class MarketDataDaoIntegrationTest {

    @Autowired
    private MarketDataDao marketDataDao;

    @Autowired
    private QuoteDao quoteDao;

    @Test
    void MarketDataDao_findByIdTest_fetchRecordFromIex() {

        String ticker = "MSFT";
        IexQuote testQuote = marketDataDao.findById(ticker).orElse(null);
        assert testQuote != null;
        assertEquals(ticker, testQuote.getSymbol());

        String invalidTicker = "NO WAY THIS IS CORRECT";
        assertTrue(marketDataDao.findById(invalidTicker).isEmpty());
    }

    @Test
    void MarketDataDao_findAllByIdTest_fetchRecordsFromIex() {

        List<String> quoteStrings = Collections.singletonList("MSFT,AAPL,GOOG");

        List<IexQuote> iexQuoteList = (List<IexQuote>) marketDataDao.findAllById(quoteStrings);
        assertEquals(3, iexQuoteList.size());
        assertEquals("MSFT", iexQuoteList.get(0).getSymbol());

        // Should result in only one company quote
        List<String> moreQuoteStrings = Collections.singletonList("XXXXXXXXXXXXXXXXX,AAPL");

        List<IexQuote> moreIexQuotes = (List<IexQuote>) marketDataDao.findAllById(moreQuoteStrings);
        assertEquals(1, moreIexQuotes.size());
    }
}
