package ca.jrvs.apps.trading.marketdata;

import ca.jrvs.apps.trading.iexquote.IexQuote;
import ca.jrvs.apps.trading.quote.Quote;
import ca.jrvs.apps.trading.quote.QuoteDao;
import ca.jrvs.apps.trading.quote.QuoteService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class QuoteServiceIntegrationTest {

    @Autowired
    private MarketDataDao marketDataDao;
    @Autowired
    private QuoteDao quoteDao;
    @Autowired
    private QuoteService quoteService;
    private Quote testQuoteOne;
    private Quote testQuoteTwo;
    private Quote testQuoteThree;

    @BeforeEach
    void setup() {

        testQuoteOne = new Quote("TEST_ONE", 1000D, 995D,
                2L, 1001D, 2L);

        testQuoteTwo = new Quote("TEST_TWO", 2000D, 1995D,
                2L, 2001D, 2L);

        testQuoteThree = new Quote("TEST_THREE", 3000D, 2995D,
                2L, 3001D, 2L);

        quoteDao.save(testQuoteOne);
        quoteDao.save(testQuoteTwo);
        quoteDao.save(testQuoteThree);
    }

    @AfterEach
    void teardown() {
        quoteDao.deleteAll();
    }

    @Test
    void QuoteService_findIexQuotes_ReturnsListOfIexQuotes() {

        List<IexQuote> iexQuoteList = quoteService.findIexQuotes("MSFT,AAPL");
        assertEquals(2, iexQuoteList.size());

        iexQuoteList = quoteService.findIexQuotes("NVDA");
        assertEquals(1, iexQuoteList.size());

        iexQuoteList = quoteService.findIexQuotes("DEFINITELY CORRECT TICKER");
        assertEquals(0, iexQuoteList.size());
    }

    @Test
    void QuoteService_findIexQuoteById_ReturnsIexQuote() {

        assertEquals("MSFT", quoteService.findIexQuoteById("MSFT").getSymbol());
        assertThrows(
                IllegalArgumentException.class,
                () -> quoteService.findIexQuoteById("DEFINITELY CORRECT TICKER")
        );
    }

    @Test
    void QuoteService_updateMarketData_updatesQuoteTable() {

        // Exception because tickers are invalid inside database, so cannot update
        assertThrows(
                IllegalArgumentException.class,
                () -> quoteService.updateMarketData()
        );
    }

    @Test
    void QuoteService_saveQuotes_savesQuotes() {

        List<String> tickers = Arrays.asList("MSFT", "NVDA", "GOOG");
        List<Quote> quoteList = quoteService.saveQuotes(tickers);

        assertEquals(3, quoteList.size());
        assertEquals("MSFT", quoteList.get(0).getTicker());
        assertEquals("NVDA", quoteList.get(1).getTicker());
        assertEquals("GOOG", quoteList.get(2).getTicker());

        tickers = Arrays.asList("no name company 44", "AAPL");
        quoteList = quoteService.saveQuotes(tickers);
        assertEquals(1, quoteList.size());
        assertEquals("AAPL", quoteList.get(0).getTicker());

        tickers = Arrays.asList("no name company 1", "no name company 2");
        quoteList = quoteService.saveQuotes(tickers);
        assertEquals(0, quoteList.size());
    }

    @Test
    void QuoteService_updateQuote_updatesQuoteInsideDb() {

        Quote newQuoteWithUpdates = new Quote("TEST_ONE", 1000D, 995D,
                2L, 5555D, 2L);
        Quote updatedTestQuote = quoteService.updateQuote(newQuoteWithUpdates);
        assertEquals(5555D, updatedTestQuote.getAskPrice());

        Quote quoteToUpdate = new Quote("NOT_IN_DB", 1000D, 995D,
                2L, 1001D, 2L);
        updatedTestQuote = quoteService.updateQuote(quoteToUpdate);
        assertNull(updatedTestQuote);
    }

    @Test
    void QuoteService_addQuote_addsNewQuoteInDb() {

        Quote newTestQuote = new Quote("MSFT_TEST", 345D, 350D,
                1L, 330D, 1L);

        // Case 1: Quote ID passed and the actual Quote are both different (Client side Issue)
        Quote insertedTestQuote = quoteService.addQuote("AAPL_TEST", newTestQuote);
        assertNull(insertedTestQuote);

        // Case 2: Quote provided doesn't exist in the Iex Repository
        insertedTestQuote = quoteService.addQuote("MSFT_TEST", newTestQuote);
        assertNull(insertedTestQuote);

        // Case 3: Quote provided exists in the Iex Repository
        newTestQuote = new Quote("MSFT", 345D, 350D,
                1L, 330D, 1L);
        insertedTestQuote = quoteService.addQuote("MSFT", newTestQuote);
        assertEquals("MSFT", insertedTestQuote.getTicker());
    }

    @Test
    void QuoteService_findAllQuotes_fetchesAllRecordsFromDb() {

        List<Quote> testQuotes = quoteService.findAllQuotes();
        assertEquals(3, testQuotes.size());

        assertEquals("TEST_ONE", testQuotes.get(0).getTicker());
        assertEquals("TEST_TWO", testQuotes.get(1).getTicker());
        assertEquals("TEST_THREE", testQuotes.get(2).getTicker());
    }

    @Test
    void QuoteService_findQuoteById_returnSpecificQuote() {

        Quote testQuote = quoteService.findQuoteById("TEST_ONE");
        assertEquals("TEST_ONE", testQuote.getTicker());

        testQuote = quoteService.findQuoteById("NVDA");
        assertNull(testQuote);
    }
}
