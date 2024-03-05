package ca.jrvs.apps.stockquote.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.jrvs.apps.stockquote.dto.Quote;
import ca.jrvs.apps.stockquote.util.QuoteHttpHelper;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuoteHttpHelperTest {

    QuoteHttpHelper quoteHttpHelper;

    @BeforeEach
    void setup() {
        this.quoteHttpHelper = new QuoteHttpHelper();
    }

    @Test
    void fetchQuoteInfoTestValidJson() {

        // Creating a fake JSON String body.
        String mockJsonResponse = "{" +
            "  \"Global Quote\": {" +
            "    \"01. symbol\": \"TEST\"," +
            "    \"02. open\": \"0\"," +
            "    \"03. high\": \"0\"," +
            "    \"04. low\": \"0\"," +
            "    \"05. price\": \"0\"," +
            "    \"06. volume\": \"0\"," +
            "    \"07. latest trading day\": \"2023-10-13\"," +
            "    \"08. previous close\": \"0\"," +
            "    \"09. change\": \"0\"," +
            "    \"10. change percent\": \"0%\"" +
            "  }" +
            "}";

        ResponseBody fakeResponseBody = ResponseBody.create(mockJsonResponse,
            MediaType.get("application/json"));

        // Crafting a fake response
        Response fakeResponse = new Response.Builder()
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .request(new Request.Builder().url(
                    "https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol=TEST&datatype=json")
                .build())
            .body(fakeResponseBody)
            .build();

        // Testing the method by providing it a fake response
        try {
            Quote quote = this.quoteHttpHelper.deserializeJson(fakeResponse);
            assertEquals("TEST", quote.getTicker());
            assertEquals(0, quote.getVolume());

        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Test
    void fetchQuoteInfoTestInvalidJson() {
        // Creating a fake JSON String body.
        String mockJsonResponse = "{" +
            "  \"Global Quote\": {" +
            "  }" +
            "}";

        ResponseBody fakeResponseBody = ResponseBody.create(mockJsonResponse,
            MediaType.get("application/json"));

        // Crafting a fake response
        try (Response fakeResponse = new Response.Builder()
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .request(new Request.Builder().url(
                    "https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol=TEST&datatype=json")
                .build())
            .body(fakeResponseBody)
            .build()) {

            assertThrows(IllegalArgumentException.class,
                () -> this.quoteHttpHelper.deserializeJson(fakeResponse));
        }
    }
}
