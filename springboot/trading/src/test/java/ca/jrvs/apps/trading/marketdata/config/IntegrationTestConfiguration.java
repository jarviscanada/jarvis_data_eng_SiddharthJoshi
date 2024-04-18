package ca.jrvs.apps.trading.marketdata.config;

import ca.jrvs.apps.trading.domain.Account;
import ca.jrvs.apps.trading.domain.Quote;
import ca.jrvs.apps.trading.domain.SecurityOrder;
import ca.jrvs.apps.trading.domain.Trader;
import ca.jrvs.apps.trading.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TestConfiguration
@ActiveProfiles("test")
public class IntegrationTestConfiguration {

    @Autowired
    private QuoteDao quoteDao;
    @Autowired
    private TraderDao traderDao;
    @Autowired
    private SecurityOrderDao securityOrderDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private PositionDao positionDao;

    public void setUpQuoteData() {
        Quote testQuoteOne = new Quote("TEST_ONE", 1000D, 995D,
                2L, 1001D, 2L);

        Quote testQuoteTwo = new Quote("TEST_TWO", 2000D, 1995D,
                2L, 2001D, 2L);

        Quote testQuoteThree = new Quote("TEST_THREE", 3000D, 2995D,
                2L, 3001D, 2L);

        quoteDao.saveAll(Arrays.asList(testQuoteOne, testQuoteTwo, testQuoteThree));
    }

    public void cleanUpQuoteData() {
        quoteDao.deleteAll();
    }

    public void setUpTraderData() {

        Trader testTraderOne = new Trader(null, "Mike", "Jordan", Date.valueOf("2000-01-01"),
                "Venezula", "mikejordan@gmail.com");
        Trader testTraderTwo = new Trader(null, "Phil", "Goody", Date.valueOf("2001-06-22"),
                "Mongolia", "phil.goody@gmail.com");
        Trader testTraderThree = new Trader(null, "John", "Greenman", Date.valueOf("1991-09-14"),
                "Mozambique", "john.green@gmail.com");

        traderDao.saveAll(Arrays.asList(testTraderOne, testTraderTwo, testTraderThree));
    }

    public void cleanUpTraderData() {
        traderDao.deleteAll();
    }

    public void setUpAccountData() {

        // Get trader ids and save them (Because IDs are in sequence)
        List<Integer> intList = new ArrayList<>();
        traderDao.findAll().forEach((trader) -> intList.add(trader.getId()));

        Account accountOne = new Account(null, intList.get(0), 0);
        Account accountTwo = new Account(null, intList.get(1), 0);
        Account accountThree = new Account(null, intList.get(2), 0);

        accountDao.saveAll(Arrays.asList(accountOne, accountTwo, accountThree));
    }

    public void cleanUpAccountData() {
        accountDao.deleteAll();
    }

    public void setUpOrderData() {

        List<Integer> accountIds = new ArrayList<>();
        accountDao.findAll().forEach((account) -> accountIds.add(account.getId()));

        SecurityOrder orderOne = new SecurityOrder(null, accountIds.get(0),
                "FILLED", "TEST_ONE", 25, 10000, "Test");

        SecurityOrder orderTwo = new SecurityOrder(null, accountIds.get(1),
                "FILLED", "TEST_TWO", 10, 3000, "Test");

        SecurityOrder orderThree = new SecurityOrder(null, accountIds.get(2),
                "FILLED", "TEST_ONE", 10, 8700, "Test");

        securityOrderDao.saveAll(Arrays.asList(orderOne, orderTwo, orderThree));
    }

    public void cleanUpOrderData() {
        securityOrderDao.deleteAll();
    }
}
