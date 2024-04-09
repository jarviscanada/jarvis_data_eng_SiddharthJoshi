package ca.jrvs.apps.trading.marketdata;

import ca.jrvs.apps.trading.iexquote.IexQuote;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class MarketDataDao implements CrudRepository<IexQuote, String> {

    private final static String IEX_API_ENDPOINT = "https://api.iex.cloud/v1/data/core/quote/";
    private final WebClient.Builder client = WebClient.builder();
    @Autowired
    private MarketDataConfig marketDataConfiguration;

    @Override
    public <S extends IexQuote> S save(S entity) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public <S extends IexQuote> Iterable<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    /**
     * Converts the single company symbol (string) into a list having one element and calls the findAllById method internally
     *
     * @param s a single company symbol
     * @return optional containing IexQuote if found otherwise optional containing a null
     */
    @Override
    public Optional<IexQuote> findById(String s) {

        // Convert the string 's' into a list further passing it as a parameter to the other method
        List<IexQuote> iexQuoteList = (List<IexQuote>) findAllById(Collections.singletonList(s));

        // If company quote found (size will be 1), then get the first element, wrap the optional around it and pass it back to the service layer
        if (iexQuoteList.size() == 1) {
            return Optional.of(iexQuoteList.get(0));
        }

        // Otherwise return an optional containing a null
        return Optional.empty();
    }

    @Override
    public boolean existsById(String s) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public Iterable<IexQuote> findAll() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    /**
     * Crafts the API endpoint and then makes the request to the IexQuote API endpoint
     *
     * @param strings an iterable containing all the potential company symbols
     * @return an iterable of all IexQuote
     */
    @Override
    public Iterable<IexQuote> findAllById(Iterable<String> strings) {

        String iexApiEndpoint = IEX_API_ENDPOINT;

        // Crafting the endpoint (Internally, the compiler uses StringBuilder while concatenating after JDK 6, so already optimized)
        iexApiEndpoint += Iterables.getOnlyElement(strings) +
                "?token=" + System.getenv("IEX_SUB_TOKEN");

        // Call the endpoint (async approach)
        List<IexQuote> iexQuoteList = this.client.build()
                .get()
                .uri(iexApiEndpoint)
                .retrieve()
                .bodyToFlux(IexQuote.class)
                .collectList()
                .block();

        if (iexQuoteList == null) {

            // Yet to do: Find out the cause of why iexQuoteList is null and map it to a specific exception...
            throw new IllegalArgumentException(
                    "Data associated with Company Symbols doesn't exist. Make sure the company symbol is valid."
            );
        }

        return iexQuoteList;
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void deleteById(String s) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void delete(IexQuote entity) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void deleteAll(Iterable<? extends IexQuote> entities) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
