package ca.jrvs.apps.stockquote.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the Connection to the database
 */
public class DatabaseConnectionManager {

    private static final Logger appFlowlogger = LoggerFactory.getLogger("AppFlowLogger");
    private static String url = "jdbc:postgresql://localhost/stock_quote";
    private static final Properties databaseProperties = new Properties();

    public static void initializeConnectionParameters(String server, String database, String user,
        String password) throws IllegalArgumentException, NullPointerException {

        if (server.isEmpty() || database.isEmpty() || user.isEmpty()) {
            throw new IllegalArgumentException();
        }

        DatabaseConnectionManager.url = "jdbc:postgresql://" + server + ":5432/" + database;
        DatabaseConnectionManager.databaseProperties.setProperty("user", user);
        DatabaseConnectionManager.databaseProperties.setProperty("password", password);
        appFlowlogger.info("Database attributes set successfully.");
    }

    // Static initializer (Class Constructor) executed when class is loaded and initializes static attributes
    static {
        databaseProperties.setProperty("user", "postgres");
        databaseProperties.setProperty("password", "admin");
    }


    /**
     * Creates an actual connection with the database
     *
     * @return connection reference
     * @throws SQLException exception when JDBC fails to connect with the database.
     */
    public static Connection establishConnection() throws SQLException {
        appFlowlogger.info("Attempting to connect to the database...");
        Connection connection = DriverManager.getConnection(url, databaseProperties);
        appFlowlogger.info("Successfully connected to the database.");
        return connection;
    }

    /**
     * Creates an actual connection with the database used for testing purposes only.
     * @return connection reference to the test database.
     * @throws SQLException exception when JDBC fails to connect with the database.
     */
    public static Connection establishConnectionForTests() throws SQLException {

        // Setting up the test database.
        final String testDatabaseUrl = "jdbc:postgresql://localhost/stock_quote_test";
        final Properties testDatabaseProperties = new Properties();
        testDatabaseProperties.setProperty("user", "postgres");
        testDatabaseProperties.setProperty("password", "admin");

        return DriverManager.getConnection(testDatabaseUrl, testDatabaseProperties);
    }

    /**
     * Closes the connection to the test database (Useful for teardown methods in unit testing)
     * @param testConnection the connection reference to the test database
     * @throws SQLException error while closing the connection to the test database
     */
    public static void closeConnectionToTestDatabase(Connection testConnection) throws SQLException {
        testConnection.close();
    }
}
