package ca.jrvs.apps.trading.marketdata;

import ca.jrvs.apps.trading.dto.MarketOrderBuyDto;
import ca.jrvs.apps.trading.dto.MarketOrderSellDto;
import ca.jrvs.apps.trading.dto.OrderStatus;
import ca.jrvs.apps.trading.entity.*;
import ca.jrvs.apps.trading.exceptions.CannotPerformOperationException;
import ca.jrvs.apps.trading.exceptions.InvalidRequestException;
import ca.jrvs.apps.trading.exceptions.ResourceNotFoundException;
import ca.jrvs.apps.trading.marketdata.config.IntegrationTestConfiguration;
import ca.jrvs.apps.trading.repository.*;
import ca.jrvs.apps.trading.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import(IntegrationTestConfiguration.class)
@ActiveProfiles("test")
public class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private IntegrationTestConfiguration testConfig;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private SecurityOrderDao securityOrderDao;
    @Autowired
    private QuoteDao quoteDao;
    @Autowired
    private PositionDao positionDao;
    @Autowired
    private TraderDao traderDao;

    @BeforeEach
    void setup() {
        testConfig.setUpQuoteData();
        testConfig.setUpTraderData();
        testConfig.setUpAccountData();
        testConfig.setUpOrderData();
    }

    @AfterEach
    void teardown() {
        testConfig.cleanUpOrderData();
        testConfig.cleanUpAccountData();
        testConfig.cleanUpTraderData();
        testConfig.cleanUpQuoteData();
    }

    @Test
    void executeBuyMarketOrderTest_ThrowException() {

        // Invalid Ticker (Mostly doesn't exist)
        MarketOrderBuyDto invalidOrderOne = new MarketOrderBuyDto(94384, 10, "WRONG_TICKER");
        assertThrows(
                InvalidRequestException.class,
                () -> orderService.executeBuyMarketOrder(invalidOrderOne)
        );

        // Invalid size (0 or below)
        MarketOrderBuyDto invalidOrderTwo = new MarketOrderBuyDto(88786, 0, "TEST_ONE");
        assertThrows(
                InvalidRequestException.class,
                () -> orderService.executeBuyMarketOrder(invalidOrderTwo)
        );

        // Invalid Account ID (Account with ID 0 never exists in this application)
        MarketOrderBuyDto invalidOrderThree = new MarketOrderBuyDto(0, 10, "TEST_ONE");
        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.executeBuyMarketOrder(invalidOrderThree)
        );
    }

    @Test
    void executeBuyMarketOrderTest_InsufficientFunds() {

        // Get the Account Ids
        List<Integer> accountIds = new ArrayList<>();
        accountDao.findAll().forEach((account) -> accountIds.add(account.getId()));

        // Craft a valid record
        MarketOrderBuyDto validRecordOne = new MarketOrderBuyDto(accountIds.get(0), 10, "TEST_ONE");

        // Will fail to execute because all accounts have 0 funds by default.
        assertThrows(
                CannotPerformOperationException.class,
                () -> orderService.executeBuyMarketOrder(validRecordOne)
        );

        // Double-checking the status (Size would be two because 2 orders associated with one account)
        // First element is the OG order (Added in setup method) and second element is added in this test itself
        SecurityOrder canceledOrder = securityOrderDao.findAllByAccountId(validRecordOne.getAccountId()).get(1);
        assertEquals(String.valueOf(OrderStatus.CANCELED), canceledOrder.getStatus());
    }

    @Test
    void executeBuyMarketOrderTest_SuccessfulOperation() {

        // Get an order associated with any account
        List<SecurityOrder> securityOrders = securityOrderDao.findAll();
        int accountId = securityOrders.get(0).getAccount_id();

        // Guaranteed to execute since an order associated with account exists.
        Account account = accountDao.findById(accountId).get();

        // Updating Funds
        account.setAmount(1000000);
        accountDao.save(account);

        // Getting the ask price of the quote in question
        double askPrice = quoteDao.findById("TEST_ONE").get().getAskPrice();

        // Crafting an order associated with this account and executing the new order
        MarketOrderBuyDto validRecordOne = new MarketOrderBuyDto(account.getId(), 10, "TEST_ONE");
        SecurityOrder executedOrder = orderService.executeBuyMarketOrder(validRecordOne);

        // Fetching the updated account details (Successful deduction or not)
        Account updatedAccount = accountDao.findById(account.getId()).get();

        double expectedRemainingAmount = account.getAmount() - (askPrice * validRecordOne.getSize());
        assertEquals(expectedRemainingAmount, updatedAccount.getAmount());
        assertEquals(String.valueOf(OrderStatus.FILLED), executedOrder.getStatus());
    }

    @Test
    void executeSellMarketOrderTest_ThrowCantPerformOpException() {

        // Creating a new trader and an account with 0 funds, so it will have no portfolio
        Trader testTrader = new Trader(null, "Mackenzie", "Johnson", Date.valueOf("2000-01-01"),
                "Canada", "mackenzie.johnson@gmail.com");
        testTrader = traderDao.save(testTrader);

        Account testAccount = new Account(null, testTrader.getId(), 0);
        testAccount = accountDao.save(testAccount);

        MarketOrderSellDto testSellOrder = new MarketOrderSellDto(testAccount.getId(), "MSFT");
        assertThrows(
                CannotPerformOperationException.class,
                () -> orderService.executeSellMarketOrder(testSellOrder)
        );
    }

    @Test
    void executeSellMarketOrderTest_ProcessOrder() {

        // Get the existing trader from the test data
        List<Account> accounts = new ArrayList<>(accountDao.findAll());

        List<Integer> accountIds = new ArrayList<>();
        accounts.forEach((account) -> accountIds.add(account.getId()));

        // Getting the existing quotes
        List<Quote> quotes = new ArrayList<>(quoteDao.findAll());

        // Test sell order
        MarketOrderSellDto testSellOrder = new MarketOrderSellDto(
                accountIds.get(0), quotes.get(0).getTicker()
        );
        Position testPosition = positionDao.findByAccountIdAndTicker(accountIds.get(0), quotes.get(0).getTicker()).get();
        double expectedFunds = quotes.get(0).getBidPrice() * testPosition.getPosition();
        assertEquals(expectedFunds, orderService.executeSellMarketOrder(testSellOrder).getAmountGained());

        testSellOrder = new MarketOrderSellDto(accountIds.get(1), quotes.get(1).getTicker());
        testPosition = positionDao.findByAccountIdAndTicker(accountIds.get(1), quotes.get(1).getTicker()).get();
        expectedFunds = quotes.get(1).getBidPrice() * testPosition.getPosition();
        assertEquals(expectedFunds, orderService.executeSellMarketOrder(testSellOrder).getAmountGained());

        testSellOrder = new MarketOrderSellDto(accountIds.get(2), quotes.get(0).getTicker());
        testPosition = positionDao.findByAccountIdAndTicker(accountIds.get(2), quotes.get(0).getTicker()).get();
        expectedFunds = quotes.get(0).getBidPrice() * testPosition.getPosition();
        assertEquals(expectedFunds, orderService.executeSellMarketOrder(testSellOrder).getAmountGained());
    }
}
