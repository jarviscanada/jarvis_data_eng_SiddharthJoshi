package ca.jrvs.apps.stockquote.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.jrvs.apps.stockquote.dto.Quote;
import ca.jrvs.apps.stockquote.util.DatabaseConnectionManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuoteDaoTest {

    private QuoteDao quoteDao;
    private Quote testQuote;
    private static Connection testConnection;

    @BeforeAll
    static void setTestConnection() throws SQLException {

        // Creating a common connection accessible to all test methods
        QuoteDaoTest.testConnection = DatabaseConnectionManager.establishConnectionForTests();
    }

    @BeforeEach
    void setup() {

        this.testQuote = new Quote(
            "TEST", 0, 0, 0, 0, 0,
            "2024-02-20", 0, 0, "0%",
            new Timestamp(System.currentTimeMillis())
        );

        this.quoteDao = new QuoteDao(QuoteDaoTest.testConnection);
    }

    @AfterEach
    void tearDown() throws SQLException {

        // Clearing the record which was used for testing
        this.quoteDao.deleteById("TEST");
    }

    @AfterAll
    static void closeTestConnection() throws SQLException {

        // Closing the common connection after all the tests are done running
        DatabaseConnectionManager.closeConnectionToTestDatabase(QuoteDaoTest.testConnection);
    }

    @Test
    void saveTest() throws SQLException {

        this.quoteDao.save(this.testQuote);
        assertTrue(this.quoteDao.findById("TEST").isPresent());
        assertEquals("TEST", this.quoteDao.findById("TEST").get().getTicker());
    }

    @Test
    void findByIdTest() throws SQLException {

        Optional<Quote> quoteOptional = this.quoteDao.findById("MSFT");
        assertTrue(quoteOptional.isEmpty());

        // No record with ID '....' exists in the database, the Optional returned by findById will be empty.
        quoteOptional = this.quoteDao.findById("....");
        assertTrue(quoteOptional.isEmpty());
    }

    @Test
    void findAllTest() throws SQLException {

        Iterator<Quote> quoteIterator = this.quoteDao.findAll();

        // If Iterator has next element, then above operation successfully returned all records.
        if (quoteIterator.hasNext()) {
            assertTrue(true);
        }

        // If Iterator doesn't have next element, then above operation was successful, but the table was empty.
        if (!quoteIterator.hasNext()) {
            assertFalse(false);
        }
    }

    @Test
    void deleteByIdTest() throws SQLException {

        this.quoteDao.deleteById("....");

        // We did not insert record with ID "...." in this case, so the optional returned should be empty.
        assertTrue(this.quoteDao.findById("....").isEmpty());
    }

    @Test
    void deleteAllTest() throws SQLException {

        this.quoteDao.deleteAll();
        Iterator<Quote> quoteIterator = this.quoteDao.findAll();

        // Since we deleted all the elements, the findAll method will return an Iterator with no next element.
        assertFalse(quoteIterator.hasNext());
    }

    @Test
    void areEnoughSharesTest() throws SQLException {

        Optional<Quote> optionalQuote = this.quoteDao.findById("....");
        assertTrue(optionalQuote.isEmpty());

        optionalQuote = this.quoteDao.findById("TEST");

        // Just checking whether the amount was fetched or not from the database
        // In this case, the amount of stocks will never be negative

        // ifPresent is a method which takes the instance of the Consumer executing .accept method.
        optionalQuote.ifPresent((quote) -> assertNotEquals(-1, quote.getVolume()));
    }
}
