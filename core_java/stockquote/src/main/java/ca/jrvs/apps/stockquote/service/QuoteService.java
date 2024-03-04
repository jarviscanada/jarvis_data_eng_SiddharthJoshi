package ca.jrvs.apps.stockquote.service;

import ca.jrvs.apps.stockquote.dao.QuoteDao;
import ca.jrvs.apps.stockquote.dto.Quote;
import ca.jrvs.apps.stockquote.util.QuoteHttpHelper;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteService {

    private QuoteDao quoteDao;
    private final QuoteHttpHelper quoteHttpHelper;
    private static final Logger appFlowlogger = LoggerFactory.getLogger("AppFlowLogger");

    public QuoteService(QuoteHttpHelper quoteHttpHelper) {
        this.quoteHttpHelper = quoteHttpHelper;
    }

    public QuoteService(QuoteDao quoteDao, QuoteHttpHelper quoteHttpHelper) {
        this.quoteDao = quoteDao;
        this.quoteHttpHelper = quoteHttpHelper;
    }


    /**
     * Fetches the latest quote data from alpha-vantage api endpoint.
     *
     * @param ticker the company symbol
     * @return latest quote data or empty optional if no data associated with the company symbol
     */
    public Optional<Quote> fetchQuoteDataFromAPI(String ticker) throws IOException {

        appFlowlogger.info("[QUOTE SERVICE] Communicating with Utility class QuoteHttpHelper.java to get the quote information");
        Quote companyQuote = this.quoteHttpHelper.fetchQuoteInfo(ticker);
        return Optional.ofNullable(companyQuote);
    }

    /**
     * Processes a buy order and updates the database accordingly
     *
     * @param ticker         the company symbol of which the stocks are being bought
     * @param numberOfShares number of shares to buy
     * @return updated instance of Position
     */
    public boolean areEnoughShares(String ticker, int numberOfShares) throws SQLException {

        appFlowlogger.info("[QUOTE SERVICE] Communicating with the DAO layer [QuoteDao.java] to check the volume of shares");
        Optional<Quote> quoteOptional = this.quoteDao.findById(ticker);

        if (quoteOptional.isPresent()) {
            appFlowlogger.info("[QUOTE SERVICE] Quote exists. Now checking whether sufficient amount exists or not before proceeding with the transaction");
            return quoteOptional.get().getVolume() >= numberOfShares;
        }

        // Future Enhancement: Auto-add the missing stock inside the 'quote' table.
        appFlowlogger.info("[QUOTE SERVICE] Quote doesn't exist. Returning false to the POSITION SERVICE");
        System.out.println("Stock associated with " + ticker + " doesn't exist. Cannot proceed with the transaction.");
        return false;
    }

    public Optional<Quote> getSpecificQuote(String ticker) throws SQLException {

        appFlowlogger.info("[QUOTE SERVICE] Communicating with the DAO layer [QuoteDao.java] to find the record with proper ID");
        return this.quoteDao.findById(ticker);
    }

    public void updateStockData() throws SQLException, RuntimeException {

        appFlowlogger.info("[QUOTE SERVICE] Communicating with the DAO layer [QuoteDao.java] to fetch all the records from the database");
        Iterator<Quote> quoteIterator = this.quoteDao.findAll();
        quoteIterator.forEachRemaining((quote) -> {
            try {

                // For each Company Quote (DTO), get the company symbol, get its latest data and map it to the new DTO
                Quote latestCompanyData = this.quoteHttpHelper.fetchQuoteInfo(quote.getTicker());

                // Pass that DTO to QuoteDao to update the data inside the database
                this.quoteDao.save(latestCompanyData);

            } catch (IOException | SQLException possibleExceptions) {
                throw new RuntimeException(possibleExceptions);
            }
        });
    }
}
