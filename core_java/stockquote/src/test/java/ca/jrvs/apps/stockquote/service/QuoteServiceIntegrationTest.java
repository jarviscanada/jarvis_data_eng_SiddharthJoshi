package ca.jrvs.apps.stockquote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.jrvs.apps.stockquote.Main;
import ca.jrvs.apps.stockquote.dao.QuoteDao;
import ca.jrvs.apps.stockquote.dto.Quote;
import ca.jrvs.apps.stockquote.util.DatabaseConnectionManager;
import ca.jrvs.apps.stockquote.util.QuoteHttpHelper;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Properties;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QuoteServiceIntegrationTest {

    private static QuoteDao quoteDao;
    private QuoteService quoteService;
    private QuoteHttpHelper quoteHttpHelper;
    private static Connection testConnection;
    private static String apiKey;

    @BeforeAll
    static void makeTestConnection() throws SQLException, IOException {

        // Making the connection to the test database
        QuoteServiceIntegrationTest.testConnection = DatabaseConnectionManager.establishConnectionForTests();

        // Creating an actual working instance of QuoteDao class
        QuoteServiceIntegrationTest.quoteDao = new QuoteDao(QuoteServiceIntegrationTest.testConnection);

        // Adding the Specific Share inside the test database
        Quote testQuote = new Quote(
            "PROTOTYPE", 0, 0, 0, 0, 100, "2024-02-28",
            0, 0, "0%", new Timestamp(System.currentTimeMillis())
        );

        QuoteServiceIntegrationTest.quoteDao.save(testQuote);

        // Reading Properties file for api-key
        InputStream inputStream = Main.class.getResourceAsStream("/properties.txt");
        Properties properties = new Properties();
        properties.load(inputStream);
        QuoteServiceIntegrationTest.apiKey = properties.getProperty("api-key");
    }

    @BeforeEach
    void setup() {

        // Creating an actual working instance of QuoteHttpHelper instead of mocked one (Integration Test)
        this.quoteHttpHelper = new QuoteHttpHelper(QuoteServiceIntegrationTest.apiKey, new OkHttpClient());
        this.quoteService = new QuoteService(QuoteServiceIntegrationTest.quoteDao, this.quoteHttpHelper);
    }

    @AfterEach
    void teardown() {

    }

    @AfterAll
    static void closeTestConnection() throws SQLException {

        QuoteServiceIntegrationTest.quoteDao.deleteById("PROTOTYPE");

        // Closing the common connection after all the tests are done running
        DatabaseConnectionManager.closeConnectionToTestDatabase(QuoteServiceIntegrationTest.testConnection);
    }

    @Test
    void fetchQuoteDataFromAPITest() throws IOException {

        // Calling the actual method
        this.quoteService = new QuoteService(quoteHttpHelper);

        Optional<Quote> quoteOptional = this.quoteService.fetchQuoteDataFromAPI("MSFT");
        assertTrue(quoteOptional.isPresent());
        assertEquals("MSFT", quoteOptional.get().getTicker());
        assertThrows(IllegalArgumentException.class, () -> this.quoteService.fetchQuoteDataFromAPI("...."));
    }

    @Test
    void areEnoughSharesTest() throws SQLException {

        boolean result = this.quoteService.areEnoughShares("PROTOTYPE", 99);
        assertTrue(result);

        result = this.quoteService.areEnoughShares("PROTOTYPE", 1001);
        assertFalse(result);
    }

    @Test
    void getSpecificQuoteTest() throws SQLException {

        assertTrue(QuoteServiceIntegrationTest.quoteDao.findById("PROTOTYPE").isPresent());
        assertFalse(QuoteServiceIntegrationTest.quoteDao.findById("INVALID-ID").isPresent());
    }

    @Test
    void updateStockDataTest() throws SQLException {

        // Making an updated record of 'PROTOTYPE' instead of fetching it from 3rd-party endpoint
        Quote updatedTestQuote = new Quote(
            "PROTOTYPE", 5, 5, 5, 5, 500, "2024-02-29",
            5, 5, "5%", new Timestamp(System.currentTimeMillis())
        );

        Quote storedUpdatedQuote = QuoteServiceIntegrationTest.quoteDao.save(updatedTestQuote);
        assertEquals(500, storedUpdatedQuote.getVolume());
        assertEquals("5%", storedUpdatedQuote.getChangePercent());
    }
}
