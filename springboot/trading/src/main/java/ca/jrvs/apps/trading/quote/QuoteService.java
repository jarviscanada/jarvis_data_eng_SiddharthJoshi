package ca.jrvs.apps.trading.quote;

import ca.jrvs.apps.trading.iexquote.IexQuote;
import ca.jrvs.apps.trading.marketdata.MarketDataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class QuoteService {

    @Autowired
    private MarketDataDao marketDataDao;
    @Autowired
    private QuoteDao quoteDao;

    public List<IexQuote> findIexQuotes(String ticker) {

        // If "," exists, there is potentially a request for multiple company quotes. So in that case, call findAllById method in the repository layer
        if (ticker.contains(",")) {
            return (List<IexQuote>) marketDataDao.findAllById(Collections.singletonList(ticker));
        }

        // Otherwise, there is a request for just one company quote. So, can call findById in the repository layer
        return marketDataDao.findById(ticker).stream().toList();
    }

    /**
     * Get all the existing quotes from the `Quote` table in the database, compare it with the latest market data quotes,
     * update and save the updated data in the database
     */
    public void updateMarketData() {

        this.quoteDao.findAll().forEach((quote) -> {
            IexQuote updatedIexQuote = this.marketDataDao.findById(quote.getTicker()).orElseThrow(() ->
                    new IllegalArgumentException("Invalid Ticker inside the Database: " + quote.getTicker()));
            Quote updatedQuote = buildQuoteFromIexQuote(updatedIexQuote);
            this.quoteDao.save(updatedQuote);
        });
    }

    /**
     * Get all the IexQuotes from the endpoint, convert to Quote entity and save the quote entity in the database
     * @param tickers list of company tickers / symbols
     * @return list of saved quotes
     */
    public List<Quote> saveQuotes(List<String> tickers) {

        List<Quote> quoteList = new ArrayList<>();
        this.marketDataDao.findAllById(tickers).forEach((iexQuote) -> {
            Quote newQuote = buildQuoteFromIexQuote(iexQuote);
            quoteList.add(this.quoteDao.save(newQuote));
        });

        return quoteList;
    }

    /**
     * finds an IexQuote from a given ticker
     * @param ticker company symbol
     * @return instance of the IexQuote
     */
    public IexQuote findIexQuoteById(String ticker) {
        return this.marketDataDao.findById(ticker).orElseThrow(() -> new IllegalArgumentException("Invalid ticker"));
    }

    /**
     * updates a quote to the database with validation
     * @param quote quote to be updated
     * @return inserted quote
     */
    public Quote updateQuote(Quote quote) {

        // Checking whether the quote to update exists in the database or not
        if (quoteDao.findById(quote.getTicker()).isEmpty()) {
            return null;
        }
        return this.quoteDao.save(quote);
    }

    /**
     * Validate the quote from the client side ot the IEX system. If exists, add it to the database otherwise don't perform the operation
     * @param newQuote new quote to be added in the databaser
     * @return instance of the quote added
     */
    public Quote addQuote(String tickerId, Quote newQuote) {

        // Throw invalid request exception
        if (!tickerId.equals(newQuote.getTicker())) {

            // Yet to do
            return null;
        }

        // Checking whether the company data exists in Iex Market or not
        if (marketDataDao.findById(tickerId).isEmpty()) {
            System.out.println("Quote not Found in the IEX System. So, most probably the company symbol is not valid...");
            return null;
        }

        return quoteDao.save(newQuote);
    }

    /**
     * find all quotes from the quote table
     * @return list of all quotes
     */
    public List<Quote> findAllQuotes() {
        return this.quoteDao.findAll();
    }

    /**
     * fetches the quote from the quote table if exists
     * @param tickerId company symbol
     * @return instance of the quote
     */
    public Quote findQuoteById(String tickerId) {
        return quoteDao.findById(tickerId).orElse(null);
    }

    /**
     * Helper method to map an IexQuote to Quote entity
     * @param iexQuote IexQuote from the IEX endpoint
     * @return Quote instance
     */
    protected static Quote buildQuoteFromIexQuote(IexQuote iexQuote) {

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
}
