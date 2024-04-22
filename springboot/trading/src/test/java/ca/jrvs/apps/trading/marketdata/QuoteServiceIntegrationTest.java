package ca.jrvs.apps.trading.marketdata;

import ca.jrvs.apps.trading.dao.MarketDataDao;
import ca.jrvs.apps.trading.dto.IexQuote;
import ca.jrvs.apps.trading.exceptions.InvalidRequestException;
import ca.jrvs.apps.trading.exceptions.ResourceNotFoundException;
import ca.jrvs.apps.trading.exceptions.UnknownDataException;
import ca.jrvs.apps.trading.marketdata.config.IntegrationTestConfiguration;
import ca.jrvs.apps.trading.entity.Quote;
import ca.jrvs.apps.trading.repository.QuoteDao;
import ca.jrvs.apps.trading.service.QuoteService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(IntegrationTestConfiguration.class)
@ActiveProfiles("test")
public class QuoteServiceIntegrationTest {

    @Autowired
    private MarketDataDao marketDataDao;
    @Autowired
    private QuoteDao quoteDao;
    @Autowired
    private QuoteService quoteService;
    @Autowired
    private IntegrationTestConfiguration testConfig;

    @BeforeEach
    public void setup() {
        testConfig.setUpQuoteData();
    }

    @AfterEach
    public void tearDown() {
        testConfig.cleanUpQuoteData();
    }

    @Test
    void findIexQuotesTest_ReturnsListOfIexQuotes() {

        List<IexQuote> iexQuoteList = quoteService.findIexQuotes("MSFT,AAPL");
        assertEquals(2, iexQuoteList.size());

        iexQuoteList = quoteService.findIexQuotes("NVDA");
        assertEquals(1, iexQuoteList.size());

        assertThrows(
                ResourceNotFoundException.class,
                () -> quoteService.findIexQuotes("DEFINITELY CORRECT TICKER")
        );
    }

    @Test
    void findIexQuoteByIdTest_ReturnsIexQuote() {

        assertEquals("MSFT", quoteService.findIexQuoteById("MSFT").getSymbol());
        assertThrows(
                ResourceNotFoundException.class,
                () -> quoteService.findIexQuoteById("DEFINITELY CORRECT TICKER")
        );
    }

    @Test
    void updateMarketDataTest_updatesQuoteTable() {

        // Exception because tickers are invalid inside database, so cannot update
        assertThrows(
                UnknownDataException.class,
                () -> quoteService.updateMarketData()
        );
    }

    @Test
    void saveQuotesTest_savesQuotes() {

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
    void updateQuoteTest_updatesQuoteInsideDb() {

        Quote newQuoteWithUpdates = new Quote("TEST_ONE", 1000D, 995D,
                2L, 5555D, 2L);
        Quote updatedTestQuote = quoteService.updateQuote(newQuoteWithUpdates);
        assertEquals(5555D, updatedTestQuote.getAskPrice());

        Quote quoteToUpdate = new Quote("NOT_IN_DB", 1000D, 995D,
                2L, 1001D, 2L);
        assertThrows(
                ResourceNotFoundException.class,
                () -> quoteService.updateQuote(quoteToUpdate)
        );
    }

    @Test
    void addQuoteTest_addsNewQuoteInDb() {

        Quote newTestQuoteOne = new Quote("MSFT_TEST", 345D, 350D,
                1L, 330D, 1L);

        // Case 1: Quote ID passed and the actual Quote are both different (Client side Issue)
        assertThrows(
                InvalidRequestException.class,
                () -> quoteService.addQuote("AAPL_TEST", newTestQuoteOne)
        );

        // Case 2: Quote provided doesn't exist in the Iex Repository
        assertThrows(
                InvalidRequestException.class,
                () -> quoteService.addQuote("MSFT_TEST", newTestQuoteOne)
        );

        // Case 3: Quote provided exists in the Iex Repository
        Quote newTestQuoteTwo = new Quote("MSFT", 345D, 350D,
                1L, 330D, 1L);
        Quote insertedTestQuote = quoteService.addQuote("MSFT", newTestQuoteTwo);
        assertEquals("MSFT", insertedTestQuote.getTicker());
    }

    @Test
    void findAllQuotesTest_fetchesAllRecordsFromDb() {

        List<Quote> testQuotes = quoteService.findAllQuotes();

        assertEquals(3, testQuotes.size());
        assertEquals("TEST_ONE", testQuotes.get(0).getTicker());
        assertEquals("TEST_TWO", testQuotes.get(1).getTicker());
        assertEquals("TEST_THREE", testQuotes.get(2).getTicker());
    }

    @Test
    void findQuoteByIdTest_returnSpecificQuote() {

        Quote testQuote = quoteService.findQuoteById("TEST_ONE");
        assertEquals("TEST_ONE", testQuote.getTicker());

        assertThrows(
                ResourceNotFoundException.class,
                () -> quoteService.findQuoteById("NVDA")
        );
    }
}
