package ca.jrvs.apps.stockquote.util;

import ca.jrvs.apps.stockquote.dto.Quote;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteHttpHelper {

    private String apiKey;
    private OkHttpClient httpClient;
    private static final Logger appFlowlogger = LoggerFactory.getLogger("AppFlowLogger");
    private static final Logger errorLogger = LoggerFactory.getLogger("ErrorLogger");
    public QuoteHttpHelper(String apiKey, OkHttpClient client) {
        this.apiKey = apiKey;
        this.httpClient = client;
    }

    public QuoteHttpHelper() {

    }

    /**
     * fetches the latest quote data from Alpha Vantage api endpoint
     *
     * @param symbol the company symbol whose quote is to be fetched
     * @return latest data
     * @throws IllegalArgumentException if no data was found for the given symbol
     */
    public Quote fetchQuoteInfo(String symbol) throws IllegalArgumentException, IOException {

        // Crafting the HTTP Request
        Request request = new Request.Builder()
            .url("https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol=" + symbol
                + "&datatype=json")
            .get()
            .addHeader("X-RapidAPI-Key", this.apiKey)
            .addHeader("X-RapidAPI-Host", "alpha-vantage.p.rapidapi.com")
            .build();

        appFlowlogger.info("[UTILITY - QuoteHttpHelper] - Requested successfully created. Attempting to send the HTTP Request to the endpoint");

        // Sending the HTTP Request
        try (Response httpResponse = this.httpClient.newCall(request).execute()) {
            if (!httpResponse.isSuccessful()) {
                System.out.println("Request failed with code " + httpResponse.code());
                return null;
            }
            appFlowlogger.info("[UTILITY - QuoteHttpHelper] - Request Successful. Got the Response back");
            return deserializeJson(httpResponse);
        }
    }

    /**
     * Deserializes the response from http (JSON) to a string further mapping it to the Quote class
     *
     * @param httpResponse The response from the alpha-vantage api endpoint
     * @return instance of the Quote class
     * @throws IOException situation when there is error processing json or mapping
     */
    public Quote deserializeJson(Response httpResponse) throws IOException {

        appFlowlogger.info("[UTILITY - QuoteHttpHelper] - Attempting to map the JSON response to its corresponding DTO");
        String jsonData = httpResponse.body().string();
        ObjectMapper mapper = new ObjectMapper();

        // Fetching the root node
        JsonNode rootNode = mapper.readTree(jsonData);

        // Fetching the "Global Quote" node
        JsonNode globalQuoteNode = rootNode.path("Global Quote");
        if (globalQuoteNode.isEmpty()) {
            errorLogger.info("No data associated with the symbol was found.");
            throw new IllegalArgumentException(
                "No data for the symbol was found. Make sure the symbol is valid");
        }
        String globalQuote = globalQuoteNode.toPrettyString();

        // Mapping the entire "Global Quote" node to the Quote class
        return mapper.readValue(globalQuote, Quote.class);
    }
}
