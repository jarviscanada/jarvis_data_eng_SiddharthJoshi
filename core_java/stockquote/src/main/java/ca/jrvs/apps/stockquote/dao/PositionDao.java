package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.dto.Position;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionDao implements CrudDao<Position, String> {
    private static final Logger appFlowlogger = LoggerFactory.getLogger("AppFlowLogger");
    private static final String INSERT_STATEMENT = "INSERT INTO position (symbol, number_of_shares, value_paid) VALUES (?, ?, ?)";
    private static final String GET_BY_ID = "SELECT * FROM position WHERE symbol = ?";
    private static final String SELECT_ALL = "SELECT * FROM position";
    private static final String DELETE_BY_ID = "DELETE FROM position WHERE symbol = ?";
    private static final String DELETE_ALL = "DELETE FROM position";
    private static final String UPDATE_RECORD = "UPDATE position SET number_of_shares = ?, value_paid = ? WHERE symbol = ?";
    private final Connection connection;

    // Utilizing the connection from DatabaseConnectionManager class.
    public PositionDao(Connection connection) {
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
    public Position save(Position entity) throws IllegalArgumentException, SQLException {

        // Checking whether the record exists or not with the help of Optional returned by `findById` method
        appFlowlogger.info("[DAO LAYER - PositionDao] Communicating with the database (Calling findById() method) to find whether the record is existing or not");
        if (findById(entity.getTicker()).isEmpty()) {
            appFlowlogger.info("[DAO LAYER - PositionDao] Record doesn't exist. Attempting to insert a fresh new record");
            try (PreparedStatement pInsertStatement = this.connection.prepareStatement(
                INSERT_STATEMENT)) {
                pInsertStatement.setString(1, entity.getTicker());
                pInsertStatement.setInt(2, entity.getNumOfShares());
                pInsertStatement.setDouble(3, entity.getValuePaid());
                pInsertStatement.execute();
                appFlowlogger.info("[DAO LAYER - PositionDao] Insertion successful. New record successfully inserted to `position` table");
                return entity;
            }
        }

        appFlowlogger.info("[DAO LAYER - PositionDao] Record already exists inside the `position` table. Attempting to update the record in `position` table");
        // Otherwise the record already exists and the user is just trying to add more shares.
        try (PreparedStatement pUpdateStatement = this.connection.prepareStatement(UPDATE_RECORD)) {

            // Get the existing position record from the database and map it to the `position` instance (Wrapped by Optional)
            Optional<Position> optionalPosition = findById(entity.getTicker());

            // Updating the values inside the database by adding the values from both entities.
            pUpdateStatement.setInt(1, optionalPosition.get().getNumOfShares() + entity.getNumOfShares());
            pUpdateStatement.setDouble(2, optionalPosition.get().getValuePaid() + entity.getValuePaid());
            pUpdateStatement.setString(3, entity.getTicker());
            pUpdateStatement.execute();
            appFlowlogger.info("[DAO LAYER - PositionDao] Successfully updated the record inside the `position` table");

            // Update the entity if the update is successful
            entity.setNumOfShares(entity.getNumOfShares() + optionalPosition.get().getNumOfShares());
            entity.setValuePaid(entity.getValuePaid() + optionalPosition.get().getValuePaid());
        }

        // Return the Updated Entity
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
    public Optional<Position> findById(String s) throws IllegalArgumentException, SQLException {

        Position position = new Position();
        appFlowlogger.info("[DAO LAYER - PositionDao] Attempting to find a specific record inside `position` table.");
        try (PreparedStatement pGetByIdStatement = this.connection.prepareStatement(GET_BY_ID);) {
            pGetByIdStatement.setString(1, s);
            ResultSet record = pGetByIdStatement.executeQuery();

            // Can use if statement instead of while since we know for sure that ResultSet from this query will only have one record.
            if (record.next()) {
                position.setTicker(record.getString(1));
                position.setNumOfShares(record.getInt(2));
                position.setValuePaid(record.getDouble(3));

                appFlowlogger.info("[DAO LAYER - PositionDao] Record found inside the position table. Returning optional to the SERVICE LAYER");
                // Wrapping the Optional around the actual data and returning it
                return Optional.of(position);
            }
        }

        // Return an empty Optional if no data is found in database
        appFlowlogger.info("[DAO LAYER - PositionDao] Record was not found inside the position table. Returning an empty optional to the SERVICE LAYER");
        return Optional.empty();
    }

    /**
     * Retrieves all entities
     *
     * @return All entities
     */
    @Override
    public Iterator<Position> findAll() throws SQLException {

        List<Position> listOfPositions;
        appFlowlogger.info("[DAO LAYER - PositionDao] Attempting to fetch all records from the database.");
        try (PreparedStatement getAllStatement = this.connection.prepareStatement(SELECT_ALL)) {
            ResultSet allRecords = getAllStatement.executeQuery();
            listOfPositions = new ArrayList<>();

            // Iterating through each record and mapping it to its corresponding DTO.
            while (allRecords.next()) {
                Position position = new Position();
                position.setTicker(allRecords.getString(1));
                position.setNumOfShares(allRecords.getInt(2));
                position.setValuePaid(allRecords.getDouble(3));
                listOfPositions.add(position);
            }
        }
        appFlowlogger.info("[DAO LAYER - PositionDao] Finding all records operation performed successfully. Returning the result back to the SERVICE LAYER");
        return listOfPositions.iterator();
    }

    /**
     * Deletes the entity with the given id. If the entity is not found, it is silently ignored
     *
     * @param s - must not be null
     * @throws IllegalArgumentException - if id is null
     */
    @Override
    public void deleteById(String s) throws IllegalArgumentException, SQLException {

        appFlowlogger.info("[DAO LAYER - PositionDao] Attempting to delete a specific record from the `position` table.");
        try (PreparedStatement deleteByIdStatement = this.connection.prepareStatement(DELETE_BY_ID);) {
            deleteByIdStatement.setString(1, s);
            deleteByIdStatement.execute();
            appFlowlogger.info("[DAO LAYER - PositionDao] Deletion successful (If existed)");
        }
    }

    /**
     * Deletes all entities managed by the repository
     */
    @Override
    public void deleteAll() throws SQLException {

        appFlowlogger.info("[DAO LAYER - PositionDao] Attempting to delete all records from the `position` table.");
        try (PreparedStatement deleteAllStatement = this.connection.prepareStatement(DELETE_ALL)) {
            deleteAllStatement.execute();
            appFlowlogger.info("[DAO LAYER - PositionDao] All records deleted successfully (If existed)");
        }
    }
}
