package ca.jrvs.apps.trading.marketdata;

import ca.jrvs.apps.trading.dto.MarketOrderDto;
import ca.jrvs.apps.trading.dto.OrderStatus;
import ca.jrvs.apps.trading.entity.Account;
import ca.jrvs.apps.trading.entity.Quote;
import ca.jrvs.apps.trading.entity.SecurityOrder;
import ca.jrvs.apps.trading.exceptions.CannotPerformOperationException;
import ca.jrvs.apps.trading.exceptions.InvalidRequestException;
import ca.jrvs.apps.trading.exceptions.ResourceNotFoundException;
import ca.jrvs.apps.trading.marketdata.config.IntegrationTestConfiguration;
import ca.jrvs.apps.trading.repository.AccountDao;
import ca.jrvs.apps.trading.repository.PositionDao;
import ca.jrvs.apps.trading.repository.QuoteDao;
import ca.jrvs.apps.trading.repository.SecurityOrderDao;
import ca.jrvs.apps.trading.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    void executeMarketOrderTest_ThrowException() {

        // Invalid Ticker (Mostly doesn't exist)
        MarketOrderDto invalidOrderOne = new MarketOrderDto(94384, 10, "WRONG_TICKER", "buy");
        assertThrows(
                InvalidRequestException.class,
                () -> orderService.executeMarketOrder(invalidOrderOne)
        );

        // Invalid size (0 or below)
        MarketOrderDto invalidOrderTwo = new MarketOrderDto(88786, 0, "TEST_ONE", "buy");
        assertThrows(
                InvalidRequestException.class,
                () -> orderService.executeMarketOrder(invalidOrderTwo)
        );

        // Invalid Account ID (Account with ID 0 never exists in this application)
        MarketOrderDto invalidOrderThree = new MarketOrderDto(0, 10, "TEST_ONE", "buy");
        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.executeMarketOrder(invalidOrderThree)
        );
    }

    @Test
    void executeMarketOrderTest_InsufficientFunds() {

        // Get the Account Ids
        List<Integer> accountIds = new ArrayList<>();
        accountDao.findAll().forEach((account) -> accountIds.add(account.getId()));

        // Craft a valid record
        MarketOrderDto validRecordOne = new MarketOrderDto(accountIds.get(0), 10, "TEST_ONE", "buy");

        // Will fail to execute because all accounts have 0 funds by default.
        assertThrows(
                CannotPerformOperationException.class,
                () -> orderService.executeMarketOrder(validRecordOne)
        );

        // Double-checking the status (Size would be two because 2 orders associated with one account)
        // First element is the OG order (Added in setup method) and second element is added in this test itself
        SecurityOrder canceledOrder = securityOrderDao.findAllByAccountId(validRecordOne.getAccountId()).get(1);
        assertEquals(String.valueOf(OrderStatus.CANCELED), canceledOrder.getStatus());
    }

    @Test
    void executeMarketOrderTest_SuccessfulOperation() {

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
        MarketOrderDto validRecordOne = new MarketOrderDto(account.getId(), 10, "TEST_ONE", "buy");
        SecurityOrder executedOrder = orderService.executeMarketOrder(validRecordOne);

        // Fetching the updated account details (Successful deduction or not)
        Account updatedAccount = accountDao.findById(account.getId()).get();

        double expectedRemainingAmount = account.getAmount() - (askPrice * validRecordOne.getSize());
        assertEquals(expectedRemainingAmount, updatedAccount.getAmount());
        assertEquals(String.valueOf(OrderStatus.FILLED) ,executedOrder.getStatus());
    }
}
