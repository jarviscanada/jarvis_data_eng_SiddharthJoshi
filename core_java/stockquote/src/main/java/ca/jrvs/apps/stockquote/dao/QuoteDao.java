package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.dto.Quote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteDao implements CrudDao<Quote, String> {

    private static final Logger appFlowlogger = LoggerFactory.getLogger("AppFlowLogger");
    private static final String INSERT_STATEMENT = "INSERT INTO quote (symbol, open, high, low, price, volume, latest_trading_day, previous_close, change, change_percent, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String GET_BY_ID = "SELECT * FROM quote WHERE symbol = ?";
    private static final String DELETE_ALL = "DELETE FROM quote";
    private static final String DELETE_BY_ID = "DELETE FROM quote WHERE symbol = ?";
    private static final String SELECT_ALL = "SELECT * FROM quote";
    private static final String UPDATE_ALL = "UPDATE quote SET open = ?, high = ?, low = ?, price = ?, volume = ?, latest_trading_day = ?, previous_close = ?, change = ?, change_percent = ?, timestamp = ? WHERE symbol = ?";
    private final Connection connection;

    // Utilizing the connection from DatabaseConnectionManager class.
    public QuoteDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Saves a given entity. Used for create and update
     *
     * @param entity - must not be null
     * @return The saved entity. Will never be null
     * @throws IllegalArgumentException - if id is null
     */
    @Override
    public Quote save(Quote entity) throws IllegalArgumentException, SQLException {

        // If the Optional returned by findById method is empty, then the quote is not present in the database
        // In this case, simply execute the Insert Statement adding the new record inside the database
        appFlowlogger.info("[DAO LAYER - QuoteDao] QuoteDao Communicating with the database (Calling findById() method) to find whether the record is existing or not");
        if (findById(entity.getTicker()).isEmpty()) {
            appFlowlogger.info("[DAO LAYER - QuoteDao] Record doesn't exist. Attempting to insert a new record");
            try (PreparedStatement insertStatement = this.connection.prepareStatement(
                INSERT_STATEMENT)) {
                insertStatement.setString(1, entity.getTicker());
                insertStatement.setDouble(2, entity.getOpen());
                insertStatement.setDouble(3, entity.getHigh());
                insertStatement.setDouble(4, entity.getLow());
                insertStatement.setDouble(5, entity.getPrice());
                insertStatement.setDouble(6, entity.getVolume());
                insertStatement.setDate(7, java.sql.Date.valueOf(entity.getLatestTradingDay()));
                insertStatement.setDouble(8, entity.getPreviousClose());
                insertStatement.setDouble(9, entity.getChange());
                insertStatement.setString(10, entity.getChangePercent());
                insertStatement.setTimestamp(11, new Timestamp(System.currentTimeMillis()));

                insertStatement.execute();
                appFlowlogger.info("[DAO LAYER] Insertion successful. New record added to `quote` table");
                return entity;
            }
        }
        appFlowlogger.info("[DAO LAYER - QuoteDao] Record already exists inside the database. Attempting to update the record in `quote` table");
        // Otherwise the company quote is already existing in the database.
        // In this case, just execute the Update statement and update the columns of that company with the latest data.
        try (PreparedStatement updateAllStatement = this.connection.prepareStatement(UPDATE_ALL)) {
            updateAllStatement.setDouble(1, entity.getOpen());
            updateAllStatement.setDouble(2, entity.getHigh());
            updateAllStatement.setDouble(3, entity.getLow());
            updateAllStatement.setDouble(4, entity.getPrice());
            updateAllStatement.setDouble(5, entity.getVolume());
            updateAllStatement.setDate(6, java.sql.Date.valueOf(entity.getLatestTradingDay()));
            updateAllStatement.setDouble(7, entity.getPreviousClose());
            updateAllStatement.setDouble(8, entity.getChange());
            updateAllStatement.setString(9, entity.getChangePercent());
            updateAllStatement.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
            updateAllStatement.setString(11, entity.getTicker());

            updateAllStatement.execute();
            appFlowlogger.info("[DAO LAYER - QuoteDao] Successfully Updated the record inside the database");
        }
        return entity;
    }

    /**
     * Retrieves an entity by its id
     *
     * @param s - must not be null
     * @return Entity with the given id or empty optional if none found
     * @throws IllegalArgumentException - if id is null
     */
    @Override
    public Optional<Quote> findById(String s) throws IllegalArgumentException, SQLException {

        Quote quote = new Quote();
        appFlowlogger.info("[DAO LAYER - QuoteDao] Attempting to find a specific record inside `quote` table.");
        try (PreparedStatement selectWithCondition = this.connection.prepareStatement(GET_BY_ID)) {
            selectWithCondition.setString(1, s);
            ResultSet resultSet = selectWithCondition.executeQuery();

            // Can use if statement instead of while since we know for sure that ResultSet from this query will only have one record.
            if (resultSet.next()) {
                quote.setTicker(resultSet.getString(1));
                quote.setOpen(resultSet.getDouble(2));
                quote.setHigh(resultSet.getDouble(3));
                quote.setLow(resultSet.getDouble(4));
                quote.setPrice(resultSet.getDouble(5));
                quote.setVolume(resultSet.getInt(6));
                quote.setLatestTradingDay(resultSet.getString(7));
                quote.setPreviousClose(resultSet.getDouble(8));
                quote.setChange(resultSet.getDouble(9));
                quote.setChangePercent(resultSet.getString(10));
                quote.setTimestamp(resultSet.getTimestamp(11));

                // Wrapping the Optional around the actual data and returning it
                appFlowlogger.info("[DAO LAYER - QuoteDao] Record found inside the quote table. Returning optional to the SERVICE LAYER");
                return Optional.of(quote);
            }
        }

        appFlowlogger.info("[DAO LAYER - QuoteDao] Record not found inside the quote table. Returning empty optional to the SERVICE LAYER");
        // Return an empty Optional if no data is found in database
        return Optional.empty();
    }

    /**
     * Retrieves all entities
     *
     * @return All entities
     */
    @Override
    public Iterator<Quote> findAll() throws SQLException {

        List<Quote> listOfQuotes;
        appFlowlogger.info("[DAO LAYER - QuoteDao - QuoteDao] Attempting to fetch all records from the database.");
        try (PreparedStatement selectAllStatement = this.connection.prepareStatement(SELECT_ALL)) {
            ResultSet allRecords = selectAllStatement.executeQuery();
            listOfQuotes = new ArrayList<>();

            // Iterating through each record mapping it to its corresponding DTO
            while (allRecords.next()) {
                Quote companyQuote = new Quote();

                // Mapping the data from the database to the Java Object via setters
                companyQuote.setTicker(allRecords.getString(1));
                companyQuote.setOpen(allRecords.getDouble(2));
                companyQuote.setHigh(allRecords.getDouble(3));
                companyQuote.setLow(allRecords.getDouble(4));
                companyQuote.setPrice(allRecords.getDouble(5));
                companyQuote.setVolume(allRecords.getInt(6));
                companyQuote.setLatestTradingDay(allRecords.getString(7));
                companyQuote.setPreviousClose(allRecords.getDouble(8));
                companyQuote.setChange(allRecords.getDouble(9));
                companyQuote.setChangePercent(allRecords.getString(10));
                companyQuote.setTimestamp(allRecords.getTimestamp(11));

                // Add the instance of the quote in the list
                listOfQuotes.add(companyQuote);
            }
        }
        appFlowlogger.info("[DAO LAYER - QuoteDao] Finding all records operation performed successfully. Returning the result back to the SERVICE LAYER");
        return listOfQuotes.iterator();
    }

    /**
     * Deletes the entity with the given id. If the entity is not found, it is silently ignored
     *
     * @param s - must not be null
     * @throws IllegalArgumentException - if id is null
     */
    @Override
    public void deleteById(String s) throws IllegalArgumentException, SQLException {

        appFlowlogger.info("[DAO LAYER - QuoteDao] Attempting to delete a specific record from the `quote` table.");
        try (PreparedStatement deleteWithConditionStmt = this.connection.prepareStatement(DELETE_BY_ID)) {
            deleteWithConditionStmt.setString(1, s);
            deleteWithConditionStmt.execute();
            appFlowlogger.info("[DAO LAYER - QuoteDao] Deletion successful (If existed)");
        }
    }

    /**
     * Deletes all entities managed by the repository
     */
    @Override
    public void deleteAll() throws SQLException {

        appFlowlogger.info("[DAO LAYER - QuoteDao] Attempting to delete all records from the `quote` table.");
        try (PreparedStatement deleteAllStatement = this.connection.prepareStatement(DELETE_ALL)) {
            deleteAllStatement.execute();
            appFlowlogger.info("[DAO LAYER - QuoteDao] All records deleted successfully (If existed)");
        }
    }
}
