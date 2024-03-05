package ca.jrvs.apps.stockquote.controller;

import ca.jrvs.apps.stockquote.dto.Quote;
import ca.jrvs.apps.stockquote.service.PositionService;
import ca.jrvs.apps.stockquote.service.QuoteService;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockQuoteController {

    private static final Logger appFlowlogger = LoggerFactory.getLogger("AppFlowLogger");
    private final QuoteService quoteService;
    private final PositionService positionService;

    public StockQuoteController(QuoteService quoteService, PositionService positionService) {
        this.quoteService = quoteService;
        this.positionService = positionService;
    }

    /**
     * This controls the flow of the entire application. Similar to the user interface of any
     * application.
     */
    public void initializeClient() throws SQLException, NumberFormatException {

        // Communicates with the Quote Service (QuoteService.java) and updates the database with the latest data from alpha-vantage
        // Commented out due to limit of API calls on the free tier account
        // this.quoteService.updateStockData();

        System.out.println("Welcome! What would you like to do?");

        // Linking the Scanner to the terminal
        Scanner terminalInput = new Scanner(System.in);
        int userChoice;

        do {
            System.out.println();
            System.out.println("1. Check your Wallet\n2. Buy Stocks\n3. Sell Stocks\n0. Exit");
            System.out.print("Choose the appropriate option (0/1/2/3): ");
            userChoice = Integer.parseInt(terminalInput.nextLine());

            switch (userChoice) {

                case 1:
                    appFlowlogger.info("[CONTROLLER] Communicating with the Quote Service. Calling 'displayAllRecords' from PositionService.java");
                    this.positionService.displayAllRecords();
                    break;

                case 2:
                    buyTransaction(terminalInput);
                    break;

                case 3:
                    sellTransaction(terminalInput);
                    break;

                case 0:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid Selection. Please enter an appropriate option to continue.");
                    break;
            }
        }
        while (userChoice != 0);
    }

    /**
     * Similar to the "Buy" button on the interface
     * @param terminalInput reference to Scanner class which reads the input from terminal
     * @throws SQLException exception while interacting with the database
     */
    public void buyTransaction(Scanner terminalInput) throws SQLException {

        System.out.print("Enter the Company Symbol for which you would like to buy the stock of: ");
        String stockToBuy = terminalInput.nextLine();

        System.out.print("Enter the amount of stocks: ");
        int numberOfSharesToBuy = Integer.parseInt(terminalInput.nextLine());

        System.out.print("Enter the total price: ");
        double totalPrice = Double.parseDouble(terminalInput.nextLine());

        int willBuy;
        appFlowlogger.info("[CONTROLLER] Communicating with the Quote Service to get a specific record");
        Optional<Quote> optionalQuote = this.quoteService.getSpecificQuote(stockToBuy);

        if (optionalQuote.isPresent()) {
            System.out.println("Below is the latest detail of the stock...");
            System.out.println(optionalQuote.get());

            System.out.println("\nWould you like to buy this Stock?");
            System.out.print("1 for Yes and 0 for No: ");
            willBuy = Integer.parseInt(terminalInput.nextLine());

            if (willBuy == 1) {
                appFlowlogger.info(
                    "[CONTROLLER] Communicating with the Position Service. Calling `buy` method PositionService.java"
                );
                this.positionService.buy(stockToBuy, numberOfSharesToBuy, totalPrice);
                System.out.println("Transaction Successful!");
            }
            return;
        }
        System.out.println("Stock associated with " + stockToBuy + " doesn't exist. Cannot proceed with the transaction.");
    }

    /**
     * Similar to the "Sell" button on a user interface
     * @param terminalInput reference to Scanner class which reads the input from terminal
     * @throws SQLException exception while interacting with the database
     */
    public void sellTransaction(Scanner terminalInput) throws SQLException {

        System.out.print("Enter the Company ID / Symbol of which you wish to sell the Stock: ");
        String companySymbol = terminalInput.nextLine();
        appFlowlogger.info("[CONTROLLER] Communicating with the Position Service. Calling 'sell' method from PositionService.java");
        this.positionService.sell(companySymbol);
    }
}
