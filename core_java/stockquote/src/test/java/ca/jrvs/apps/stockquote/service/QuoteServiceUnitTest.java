package ca.jrvs.apps.stockquote.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.jrvs.apps.stockquote.dao.QuoteDao;
import ca.jrvs.apps.stockquote.dto.Quote;
import ca.jrvs.apps.stockquote.util.QuoteHttpHelper;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuoteServiceUnitTest {

    private QuoteService quoteService;
    private QuoteHttpHelper mockedQuoteHttpHelper;
    private QuoteDao mockedQuoteDao;

    @BeforeEach
    void setup() {

        this.mockedQuoteHttpHelper = mock(QuoteHttpHelper.class);
        this.mockedQuoteDao = mock(QuoteDao.class);

        // Injecting the mocked instance inside the class due to which the code inside the service interacts with the mocked version of the dependency
        this.quoteService = new QuoteService(this.mockedQuoteDao, this.mockedQuoteHttpHelper);
    }

    @AfterEach
    void teardown() {

    }

    // Unit Test
    @Test
    void fetchQuoteDataFromAPITest_Success() throws IOException {

        Quote expectedQuote = new Quote(
            "TEST", 0.0, 0.0, 0.0, 0.0,
            0, "0", 0.0, 0.0, "0%",
            new Timestamp(System.currentTimeMillis())
        );

        // Mocking the behavior of fetchQuoteInfo method to return a Quote of our choice
        when(this.mockedQuoteHttpHelper.fetchQuoteInfo("TEST")).thenReturn(expectedQuote);

        Optional<Quote> optionalQuote = this.quoteService.fetchQuoteDataFromAPI("TEST");

        assertTrue(optionalQuote.isPresent());
        assertEquals(expectedQuote, optionalQuote.get());
        assertEquals("TEST", optionalQuote.get().getTicker());
        assertEquals("0", optionalQuote.get().getLatestTradingDay());
    }

    @Test
    void fetchQuoteDataFromAPITest_Failure() throws IOException {

        // The actual code throws IllegalArgumentException when the ticker is invalid.

        when(this.mockedQuoteHttpHelper.fetchQuoteInfo("Invalid-Ticker")).thenThrow(
            IllegalArgumentException.class);

        assertThrows(
            IllegalArgumentException.class,
            () -> this.quoteService.fetchQuoteDataFromAPI("Invalid-Ticker")
        );
    }

    @Test
    void areEnoughSharesTest() throws SQLException {

        // When we provide the original method an invalid ID, it always returns an empty optional
        when(this.mockedQuoteDao.findById("INVALID-ID")).thenReturn(Optional.empty());

        // Calling the actual method
        boolean result = this.quoteService.areEnoughShares("INVALID-ID", 10);

        verify(this.mockedQuoteDao).findById("INVALID-ID");
        assertFalse(result);

        // Crafting a dummy quote
        Quote expectedQuote = new Quote(
            "TEST", 0.0, 0.0, 0.0, 0.0,
            100, "0", 0.0, 0.0, "0%",
            new Timestamp(System.currentTimeMillis())
        );

        // Stubbing the behavior of a specific method
        when(this.mockedQuoteDao.findById("TEST")).thenReturn(Optional.of(expectedQuote));

        result = this.quoteService.areEnoughShares("TEST", 99);
        verify(this.mockedQuoteDao).findById("TEST");
        assertTrue(result);

        result = this.quoteService.areEnoughShares("TEST", 101);
        assertFalse(result);
    }

    @Test
    void getSpecificQuoteTest() throws SQLException {

        // Crafting a specific Optional
        Quote expectedQuote = new Quote(
            "TEST", 0.0, 0.0, 0.0, 0.0,
            100, "0", 0.0, 0.0, "0%",
            new Timestamp(System.currentTimeMillis())
        );

        // Stubbing the behavior of a specific method
        when(this.mockedQuoteDao.findById("TEST")).thenReturn(Optional.of(expectedQuote));

        // Calling the actual method
        Optional<Quote> result = this.quoteService.getSpecificQuote("TEST");

        // Verifying the method calls
        verify(this.mockedQuoteDao).findById("TEST");

        assertTrue(result.isPresent());
        assertEquals("TEST", result.get().getTicker());

        when(this.mockedQuoteDao.findById("INVALID-ID")).thenReturn(Optional.empty());
        result = this.quoteService.getSpecificQuote("INVALID-ID");
        verify(this.mockedQuoteDao).findById("INVALID-ID");
        assertTrue(result.isEmpty());
    }

    @Test
    void updateStockDataTest() throws SQLException, IOException {

        // Crafting a mocked list of Quote objects and adding Quote element/s inside it
        List<Quote> testListQuotes = new ArrayList<>();
        testListQuotes.add(new Quote(
            "TEST", 0.0, 0.0, 0.0, 0.0,
            100, "0", 0.0, 0.0, "0%",
            new Timestamp(System.currentTimeMillis())
        ));

        // Stubbing the behavior
        when(this.mockedQuoteDao.findAll()).thenReturn(testListQuotes.iterator());

        // Creating a new quote which demonstrates the updated quote of the company
        Quote updatedQuote = new Quote("TEST", 2.0, 2.0, 2.0, 2.0,
            2, "2", 2.0, 2.0, "2%",
            new Timestamp(System.currentTimeMillis()));

        // Making this method return an updated quote
        when(this.mockedQuoteHttpHelper.fetchQuoteInfo("TEST")).thenReturn(new Quote(
            "TEST", 2.0, 2.0, 2.0, 2.0,
            2, "2", 2.0, 2.0, "2%",
            new Timestamp(System.currentTimeMillis())
        ));

        when(this.mockedQuoteDao.save(any(Quote.class))).thenReturn(updatedQuote);
        this.quoteService.updateStockData();

        verify(this.mockedQuoteDao).findAll();
        verify(this.mockedQuoteHttpHelper).fetchQuoteInfo("TEST");
    }
}
