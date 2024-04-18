package ca.jrvs.apps.trading.marketdata;

import ca.jrvs.apps.trading.domain.Account;
import ca.jrvs.apps.trading.domain.Trader;
import ca.jrvs.apps.trading.dto.TraderAccountView;
import ca.jrvs.apps.trading.marketdata.config.IntegrationTestConfiguration;
import ca.jrvs.apps.trading.repository.AccountDao;
import ca.jrvs.apps.trading.repository.SecurityOrderDao;
import ca.jrvs.apps.trading.repository.TraderDao;
import ca.jrvs.apps.trading.service.TraderAccountService;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(IntegrationTestConfiguration.class)
@ActiveProfiles("test")
public class TraderAccountServiceIntegrationTest {

    @Autowired
    TraderAccountService traderAccountService;
    @Autowired
    private IntegrationTestConfiguration testConfig;
    @Autowired
    private TraderDao traderDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private SecurityOrderDao securityOrderDao;

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
    void createTraderAndAccount_Test() {

        Trader testTraderOne = new Trader(null, "Mike", "Judge",
                Date.valueOf("1970-02-28"), "Ecuador", "mike.judge@gmail.com");
        TraderAccountView testView = traderAccountService.createTraderAndAccount(testTraderOne);
        assertNotNull(testView.getAccount());
        assertNotNull(testView.getTrader());

        Trader testTraderTwo = new Trader(null, "Test", "Test", null, "null", "w@gmail.com");
        assertThrows(IllegalArgumentException.class, () ->
                traderAccountService.createTraderAndAccount(testTraderTwo));
    }

    @Test
    void deleteTraderById_ThrowExceptions() {

        // Passing null in traderId field
        assertThrows(
                IllegalArgumentException.class,
                () -> traderAccountService.deleteTraderById(null)
        );

        // Passing an Id of the trader which doesn't exist
        assertThrows(
                IllegalArgumentException.class,
                () -> traderAccountService.deleteTraderById(494782)
        );

        // Initialize a trader with some initial funds and attempting to delete it then
        Trader testTrader = new Trader(null, "Test", "Test", Date.valueOf("1991-09-14"),
                "Test", "test@gmail.com");
        Trader testSavedTrader = traderDao.save(testTrader);
        System.out.println(testSavedTrader.getId());
        Account testAccount = new Account(null, testSavedTrader.getId(), 500);
        accountDao.save(testAccount);

        assertThrows(IllegalArgumentException.class,
                () -> traderAccountService.deleteTraderById(testSavedTrader.getId()));
    }

    @Test
    void deleteTraderById_performDeletion() {

        // Fetching the existing traders
        List<Integer> traderIds = new ArrayList<>();
        traderDao.findAll().forEach((trader) -> traderIds.add(trader.getId()));

        traderIds.forEach((id) -> traderAccountService.deleteTraderById(id));

        assertEquals(0, securityOrderDao.findAll().size());
        assertEquals(0, accountDao.findAll().size());
        assertEquals(0, traderDao.findAll().size());
    }

    @Test
    void depositFundsTest_throwException() {

        assertThrows(
                IllegalArgumentException.class,
                () -> traderAccountService.depositFunds(null, 1000D)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> traderAccountService.depositFunds(1, 0D)
        );

        // Trader with ID 0 doesn't exist
        assertThrows(
                IllegalArgumentException.class,
                () -> traderAccountService.depositFunds(0, 10000D)
        );
    }

    @Test
    void depositFundsTest_depositFunds() {

        // Get valid IDs from existing trader (Test records)
        List<Integer> traderIds = new ArrayList<>();
        traderDao.findAll().forEach((trader) -> traderIds.add(trader.getId()));
        Integer traderId = traderIds.get(0);

        Account updatedAccount = traderAccountService.depositFunds(traderId, 10000D);
        assertEquals(10000D, updatedAccount.getAmount());

        updatedAccount = traderAccountService.depositFunds(traderId, 5000D);
        assertEquals(15000D, updatedAccount.getAmount());
    }

    @Test
    void withdrawFundsTest_throwExceptions() {

        assertThrows(
                IllegalArgumentException.class,
                () -> traderAccountService.withdrawFunds(null, 1000D)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> traderAccountService.withdrawFunds(1, 0D)
        );

        // Get valid IDs from existing trader (Test records)
        List<Integer> traderIds = new ArrayList<>();
        traderDao.findAll().forEach((trader) -> traderIds.add(trader.getId()));
        Integer traderId = traderIds.get(0);

        // The trader is initialized with 0 funds, so withdrawing should throw an exception
        assertThrows(
                IllegalArgumentException.class,
                () -> traderAccountService.withdrawFunds(traderId, 5000D)
        );
    }

    @Test
    void withdrawFundsTest_withdrawFunds() {

        // Create a new trader and account with specific funds pre-allocated
        Trader testTrader = new Trader(null, "John", "Doe", Date.valueOf("1991-09-14"),
                "Canada", "john.doe@gmail.com");
        Trader savedTestTrader = traderDao.save(testTrader);

        Account testAccount = new Account(null, savedTestTrader.getId(), 10000D);
        Account savedTestAccount = accountDao.save(testAccount);

        Account updatedTestAccount = traderAccountService.withdrawFunds(savedTestAccount.getTrader_id(), 5000D);
        assertEquals(5000D, updatedTestAccount.getAmount());

        updatedTestAccount = traderAccountService.withdrawFunds(savedTestAccount.getTrader_id(), 4999D);
        assertEquals(1D, updatedTestAccount.getAmount());

        assertThrows(
                IllegalArgumentException.class, () ->
                traderAccountService.withdrawFunds(savedTestAccount.getTrader_id(), 100D)
        );
    }
}
