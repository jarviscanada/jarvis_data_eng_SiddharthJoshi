package ca.jrvs.apps.stockquote;

import static java.lang.System.exit;

import ca.jrvs.apps.stockquote.controller.StockQuoteController;
import ca.jrvs.apps.stockquote.dao.PositionDao;
import ca.jrvs.apps.stockquote.dao.QuoteDao;
import ca.jrvs.apps.stockquote.service.PositionService;
import ca.jrvs.apps.stockquote.service.QuoteService;
import ca.jrvs.apps.stockquote.util.DatabaseConnectionManager;
import ca.jrvs.apps.stockquote.util.QuoteHttpHelper;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger appFlowlogger = LoggerFactory.getLogger("AppFlowLogger");
    private static final Logger errorLogger = LoggerFactory.getLogger("ErrorLogger");
    private static String apiKey;

    public static void main(String[] args) {

        Properties properties = new Properties();

        try (InputStream inputStream = Main.class.getResourceAsStream("/properties.txt")) {

            // Loading the Stream to the properties which will map the data inside a hashmap
            properties.load(inputStream);

        } catch (IOException ioException) {
            System.out.println("Issue from the Server Side " + ioException);
            errorLogger.error("Issue with Reading Stream", ioException);
            exit(1);

        } catch(NullPointerException nullPtrException) {
            errorLogger.error("The Input stream which contains the content of the file is null. "
                + "Likely because it was not read it or it might not have been located");
            errorLogger.error("", nullPtrException);
            System.out.println("Server Side Issue. " + nullPtrException);
            exit(1);
        }

        OkHttpClient httpClient = new OkHttpClient();
        Main.apiKey = properties.getProperty("api-key");

        // Setting up the database environment
        try {
            DatabaseConnectionManager.initializeConnectionParameters(
                properties.getProperty("server"),
                properties.getProperty("database"),
                properties.getProperty("username"),
                properties.getProperty("password")
            );
        }
        catch (IllegalArgumentException | NullPointerException possibleExceptions ) {
            System.out.println("Server Side Issue\n" + possibleExceptions);
            errorLogger.error("Some fields associated with the file might not be present. Double check the contents of that file and make sure it is being read properly", possibleExceptions);
            exit(1);
        }

        try (Connection dbConnection = DatabaseConnectionManager.establishConnection()) {

            // Initializing the Controller
            StockQuoteController controller = getStockQuoteController(
                dbConnection, httpClient);
            appFlowlogger.info("Successfully initialized Services, DAO and Controller");
            controller.initializeClient();
        }

        catch (SQLException sqlException) {
            errorLogger.error("[FATAL] ", sqlException);
            System.out.println("Server Side Error\n" + sqlException);
            exit(1);

        } catch (NumberFormatException numberFormatException) {
            errorLogger.error("[Issue from the User Side] Invalid Input Type (Expected a Number, but received something else)\n", numberFormatException);
            System.out.println("Please pass a valid number. Restart the application and pass appropriate numerical value as indicated.");
            System.out.println("Stopping...");
        }
    }

    private static StockQuoteController getStockQuoteController(Connection dbConnection,
        OkHttpClient httpClient) {

        QuoteDao quoteDao = new QuoteDao(dbConnection);
        PositionDao positionDao = new PositionDao(dbConnection);

        // Initializing the utility / helper classes
        QuoteHttpHelper quoteHttpHelper = new QuoteHttpHelper(Main.apiKey, httpClient);

        // Initializing the Services
        QuoteService quoteService = new QuoteService(quoteDao, quoteHttpHelper);
        PositionService positionService = new PositionService(quoteService, positionDao);

        // Initializing the Controller
        return new StockQuoteController(quoteService, positionService);
    }
}
