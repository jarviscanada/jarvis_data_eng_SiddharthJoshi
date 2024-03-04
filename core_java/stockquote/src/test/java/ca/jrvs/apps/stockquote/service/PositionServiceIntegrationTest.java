package ca.jrvs.apps.stockquote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.jrvs.apps.stockquote.dao.PositionDao;
import ca.jrvs.apps.stockquote.dao.QuoteDao;
import ca.jrvs.apps.stockquote.dto.Position;
import ca.jrvs.apps.stockquote.dto.Quote;
import ca.jrvs.apps.stockquote.util.DatabaseConnectionManager;
import ca.jrvs.apps.stockquote.util.QuoteHttpHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PositionServiceIntegrationTest {

    private static Connection testConnection;
    private static PositionDao positionDao;
    private  static QuoteDao quoteDao;
    private QuoteService quoteService;
    private PositionService positionService;
    private final QuoteHttpHelper quoteHttpHelper = null;

    @BeforeAll
    static void setUpResources() throws SQLException {

        // Creating a one time connection to the test database
        PositionServiceIntegrationTest.testConnection = DatabaseConnectionManager.establishConnectionForTests();

        PositionServiceIntegrationTest.positionDao = new PositionDao(testConnection);

        quoteDao = new QuoteDao(testConnection);
        quoteDao.save(new Quote(
            "PROTOTYPE", 0, 0, 0, 0, 100, "2024-02-28",
            0, 0, "0%", new Timestamp(System.currentTimeMillis())
        ));

        // Adding a record for testing
        Position testPosition2 = new Position("PROTOTYPE", 55, 24000);
        positionDao.save(testPosition2);
    }

    @AfterAll
    static void cleanUpResources() throws SQLException {

        // Cleaning up the records
        positionDao.deleteAll();
        quoteDao.deleteById("PROTOTYPE");

        // Closing the connection
        DatabaseConnectionManager.closeConnectionToTestDatabase(testConnection);
    }

    @BeforeEach
    void setup() {

        this.quoteService = new QuoteService(quoteDao, quoteHttpHelper);
        this.positionService = new PositionService(quoteService, positionDao);
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void buyTest() throws SQLException {

        Position shareBought = this.positionService.buy("PROTOTYPE", 60, 12000.00);
        assertEquals("PROTOTYPE", shareBought.getTicker());

        shareBought = this.positionService.buy("PROTOTYPE", 1000, 120000);
        assertNull(shareBought);
    }

    @Test
    void sellTest() throws SQLException {

        boolean result = this.positionService.sell("PROTOTYPE");
        assertTrue(result);

        result = this.positionService.sell("INVALID-TICKER");
        assertFalse(result);
    }
}
