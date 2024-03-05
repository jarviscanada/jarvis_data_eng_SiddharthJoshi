package ca.jrvs.apps.stockquote.service;

import ca.jrvs.apps.stockquote.dao.PositionDao;
import ca.jrvs.apps.stockquote.dto.Position;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionService {

    private static final Logger appFlowlogger = LoggerFactory.getLogger("AppFlowLogger");
    private final PositionDao positionDao;
    private final QuoteService quoteService;

    public PositionService(QuoteService quoteService, PositionDao positionDao) {
        this.positionDao = positionDao;
        this.quoteService = quoteService;
    }

    /**
     * Processes a buy order and updates the database accordingly
     *
     * @param ticker         the company symbol of which the stocks are being bought
     * @param numberOfShares number of shares to buy
     * @param price          price of each share
     * @return updated instance of Position
     */
    public Position buy(String ticker, int numberOfShares, double price) throws SQLException {

        appFlowlogger.info("[POSITION SERVICE] Creating an instance of DTO Position");
        Position position = new Position(ticker, numberOfShares, price);

        // If enough shares, perform the transaction and return the instance
        // Communicates with the other service
        appFlowlogger.info(
            "[POSITION SERVICE] Communicating with the Quote Service [QuoteService.java] to check the amount of shares inside the database");
        if (this.quoteService.areEnoughShares(ticker, numberOfShares)) {
            appFlowlogger.info("Sufficient Shares exist");
            appFlowlogger.info(
                "[POSITION SERVICE] Communicating with the DAO layer [PositionDao.java] to save the record in database (position table)");
            return this.positionDao.save(position);
        }

        appFlowlogger.info("Not Enough Shares");

        // Otherwise return null with the appropriate message
        System.out.println("Not enough shares associated with " + ticker + " company.");

        return null;
    }

    /**
     * Sell all the stocks of the given company symbol
     *
     * @param ticker the company symbol of which the stocks are being sold
     */
    public boolean sell(String ticker) throws SQLException {

        /* Check whether the ticker exists inside the Position table or not (Basically checking
           our wallet to see whether a particular stock is present or not) */
        appFlowlogger.info(
            "[POSITION SERVICE] Communicating with DAO layer (PositionDao.java) to get a particular record which user wants to sell");
        Optional<Position> positionOptional = this.positionDao.findById(ticker);
        if (positionOptional.isPresent()) {

            appFlowlogger.info(
                "[POSITION SERVICE] User has the stock. Calling DAO layer (PositionDao.java) to delete the record");
            // If it is present, simply delete the stock from the wallet (position table)
            this.positionDao.deleteById(ticker);
            System.out.println(
                "Transaction successful. All stocks of " + ticker + " successfully liquidated."
            );
            return true;
        }

        // Otherwise display the appropriate message
        else {
            System.out.println(
                "Company Stock (" + ticker
                    + ") which you are trying to sell doesn't exist inside your wallet."
            );
        }
        return false;
    }

    public void displayAllRecords() throws SQLException {

        // The service will communicate with its corresponding DAO
        appFlowlogger.info(
            "[POSITION SERVICE] Calling DAO layer to find all the records (Shares owned by User) inside the database");
        Iterator<Position> positionIterator = this.positionDao.findAll();

        // Using lambdas to access the elements via Iterator
        positionIterator.forEachRemaining((element) -> {
            System.out.println("Stock: " + element.getTicker());
            System.out.println("Number of Shares: " + element.getNumOfShares());
            System.out.println("Value Paid: " + element.getValuePaid());
            System.out.println();
        });
    }
}
