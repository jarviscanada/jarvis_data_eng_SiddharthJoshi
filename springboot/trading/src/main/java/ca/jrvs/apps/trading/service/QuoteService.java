package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.exceptions.CannotPerformOperationException;
import ca.jrvs.apps.trading.exceptions.InvalidRequestException;
import ca.jrvs.apps.trading.exceptions.ResourceNotFoundException;
import ca.jrvs.apps.trading.exceptions.UnknownDataException;
import ca.jrvs.apps.trading.repository.QuoteDao;
import ca.jrvs.apps.trading.entity.Quote;
import ca.jrvs.apps.trading.dto.IexQuote;
import ca.jrvs.apps.trading.dao.MarketDataDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class QuoteService {

    @Autowired
    private MarketDataDao marketDataDao;
    @Autowired
    private QuoteDao quoteDao;
    Logger errorLogger = LoggerFactory.getLogger(QuoteService.class);

    /**
     * Helper method to map an IexQuote to Quote entity
     *
     * @param iexQuote IexQuote from the IEX endpoint
     * @return Quote instance
     */
    protected Quote buildQuoteFromIexQuote(IexQuote iexQuote) {

        // Creating a new quote and updating it with the latest values
        Quote updatedQuote = new Quote();
        updatedQuote.setTicker(iexQuote.getSymbol());
        updatedQuote.setAskPrice((iexQuote.getIexAskPrice() == null) ? -1 : iexQuote.getIexAskPrice());
        updatedQuote.setAskSize((iexQuote.getIexAskSize() == null) ? -1 : iexQuote.getIexAskSize());
        updatedQuote.setBidPrice((iexQuote.getIexBidPrice()) == null ? -1 : iexQuote.getIexBidPrice());
        updatedQuote.setBidSize((iexQuote.getIexBidSize()) == null ? -1 : iexQuote.getIexBidSize());
        updatedQuote.setLastPrice((iexQuote.getLatestPrice()) == null ? -1 : iexQuote.getLatestPrice());

        return updatedQuote;
    }

    public List<IexQuote> findIexQuotes(String ticker) {

        // If "," exists, there is potentially a request for multiple company quotes. So in that case, call .findAllById() method in the repository layer
        if (ticker.contains(",")) {
            return (List<IexQuote>) marketDataDao.findAllById(Collections.singletonList(ticker));
        }

        // Otherwise, there is a request for just one company quote. So, can call findById in the repository layer
        Optional<IexQuote> optionalIexQuote = marketDataDao.findById(ticker);
        if (optionalIexQuote.isEmpty()) {
            errorLogger.error("Ticker is not found in the IEX ecosystem. Mostly because provided ticker (" + ticker + ") is invalid...");
            throw new ResourceNotFoundException("Data associated with " + ticker + " not found. Please make sure the ticker is valid.");
        }

        return optionalIexQuote.stream().toList();
    }

    /**
     * Get all the existing quotes from the `Quote` table in the database, compare it with the latest market data quotes,
     * update and save the updated data in the database
     */
    public void updateMarketData() {

        List<IexQuote> iexQuoteList = new ArrayList<>();

        // Check whether the existing data inside 'Quote' table is valid or not
        // Exception if the quote table contains the quote which is not in the IEX ecosystem
        this.quoteDao.findAll().forEach((quote) -> {
            iexQuoteList.add(this.marketDataDao.findById(quote.getTicker())
                    .orElseThrow(() ->
                            new UnknownDataException("Some issue with the server side.")));
        });

        // If all quotes are valid inside database, proceed to update them and save
        List<Quote> updatedQuotes = new ArrayList<>();

        iexQuoteList.forEach((iexQuote) -> updatedQuotes.add(buildQuoteFromIexQuote(iexQuote)));
        updatedQuotes.forEach((updatedQuote) -> quoteDao.save(updatedQuote));
    }

    /**
     * Get all the IexQuotes from the endpoint, convert to Quote entity and save the quote entity in the database
     *
     * @param tickers list of company tickers / symbols
     * @return list of saved quotes
     */
    public List<Quote> saveQuotes(List<String> tickers) {

        StringBuilder queryParameter = new StringBuilder();
        tickers.forEach((symbol) -> queryParameter.append(symbol).append(","));

        List<Quote> quoteList = new ArrayList<>();
        this.marketDataDao.findAllById(Collections.singletonList(String.valueOf(queryParameter)))
                .forEach((iexQuote) -> {
                    Quote newQuote = buildQuoteFromIexQuote(iexQuote);
                    quoteList.add(this.quoteDao.save(newQuote));
                });

        return quoteList;
    }

    /**
     * finds an IexQuote from a given ticker
     *
     * @param ticker company symbol
     * @return instance of the IexQuote
     */
    public IexQuote findIexQuoteById(String ticker) {

        // If the Optional is empty, throw an exception
        return this.marketDataDao.findById(ticker)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Data associated with " + ticker + " not found. Please make sure the ticker is valid."
                ));
    }

    /**
     * updates a quote to the database with validation
     *
     * @param quote quote to be updated
     * @return inserted quote
     */
    public Quote updateQuote(Quote quote) {

        // Checking whether the quote to update exists in the database or not
        if (!quoteDao.existsById(quote.getTicker())) {
            errorLogger.error("Quote to update doesn't exist inside the database. Client side issue for invalid quote");
            throw new CannotPerformOperationException("Quote which is being updated doesn't exist inside the database.");
        }

        return quoteDao.save(quote);
    }

    /**
     * Validate the quote from the client side ot the IEX system. If exists, add it to the database otherwise don't perform the operation
     *
     * @param newQuote new quote to be added in the databaser
     * @return instance of the quote added
     */
    public Quote addQuote(String tickerId, Quote newQuote) {

        if (!tickerId.equals(newQuote.getTicker())) {
            errorLogger.error("Client side issue. Request is not valid");
            throw new InvalidRequestException(
                    "Invalid Request. Please make sure Company symbol / ticker matches with the Request Body"
            );
        }

        // Checking whether the company data exists in Iex Market or not
        if (marketDataDao.findById(tickerId).isEmpty()) {
            errorLogger.error("Quote is not valid. Doesn't exist inside the IEX ecosystem. Mostly client side issue.");
            throw new InvalidRequestException(
                    "Invalid Request Body. Quote which is being added is not a valid Quote. Please double check the Quote details."
            );
        }

        return quoteDao.save(newQuote);
    }

    /**
     * find all quotes from the quote table
     *
     * @return list of all quotes
     */
    public List<Quote> findAllQuotes() {
        return this.quoteDao.findAll();
    }

    /**
     * fetches the quote from the quote table if exists
     *
     * @param tickerId company symbol
     * @return instance of the quote
     */
    public Quote findQuoteById(String tickerId) {
        return quoteDao.findById(tickerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Quote not found inside the database. Please make sure the company ticker / symbol is valid."
                ));
    }
}
