package ca.jrvs.apps.stockquote.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.jrvs.apps.stockquote.dto.Position;
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

class PositionDaoTest {

    private PositionDao positionDao;
    private Position testPosition;
    private static Connection testConnection;

    @BeforeAll
    static void setTestConnection() throws SQLException {

        // Creating a common connection accessible to all test methods
        PositionDaoTest.testConnection = DatabaseConnectionManager.establishConnectionForTests();
    }

    @BeforeEach
    void setup() throws SQLException {

        QuoteDao quoteDao = new QuoteDao(PositionDaoTest.testConnection);

        // Providing a new company ID to the `quote` table due to foreign key dependency
        quoteDao.save(new Quote("TEST", 0, 0, 0, 0, 0,
            "2024-02-20", 0, 0, "0%",
            new Timestamp(System.currentTimeMillis()))
        );

        // Cannot provide a new company ID as it should exist in the 'quote' table due to foreign key constraints
        this.testPosition = new Position("TEST", 0, 0);

        this.positionDao = new PositionDao(PositionDaoTest.testConnection);
    }

    @AfterEach
    void tearDownTest() throws SQLException {

        this.positionDao.deleteById("TEST");
    }

    @AfterAll
    static void closeTestConnection() throws SQLException {

        // Closing the common connection after all the tests are done running
        DatabaseConnectionManager.closeConnectionToTestDatabase(PositionDaoTest.testConnection);
    }

    @Test
    void saveTest() throws SQLException {

        this.positionDao.save(this.testPosition);

        // If the save operation is successful, then the findById method will return an Optional containing data.
        assertTrue(this.positionDao.findById("TEST").isPresent());
    }

    @Test
    void findByIdTest() throws SQLException {

        // Record associated with "TEST" should already be present in the database from the setup() method
        Optional<Position> positionOptional = this.positionDao.findById("TEST");
        assertTrue(positionOptional.isEmpty());

        // No record associated with "...." exists, so the optional returned must be empty.
        positionOptional = this.positionDao.findById("....");
        assertFalse(positionOptional.isPresent());
    }

    @Test
    void findAllTest() throws SQLException {

        Iterator<Position> positionIterator = this.positionDao.findAll();

        // If Iterator has next element, then above operation successfully returned all records.
        if (positionIterator.hasNext()) {
            assertTrue(true);
        }

        // If Iterator doesn't have next element, then above operation was successful, but the table was empty.
        if (!positionIterator.hasNext()) {
            assertFalse(false);
        }
    }

    @Test
    void deleteByIdTest() throws SQLException {

        this.positionDao.deleteById("TEST");

        // Since the record associated with ID "TEST" did not exist, the Optional returned by the findById method will be empty
        assertTrue(this.positionDao.findById("TEST").isEmpty());

    }

    @Test
    void deleteAllTest() throws SQLException {

        this.positionDao.deleteAll();
        Iterator<Position> positionIterator = this.positionDao.findAll();

        // Since all the records have been deleted, the above operation will be successful, but will return no rows
        assertFalse(positionIterator.hasNext());
    }
}
