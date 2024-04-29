package ca.jrvs.apps.trading.marketdata;

import ca.jrvs.apps.trading.entity.Account;
import ca.jrvs.apps.trading.entity.Trader;
import ca.jrvs.apps.trading.exceptions.ResourceNotFoundException;
import ca.jrvs.apps.trading.marketdata.config.IntegrationTestConfiguration;
import ca.jrvs.apps.trading.repository.AccountDao;
import ca.jrvs.apps.trading.repository.TraderDao;
import ca.jrvs.apps.trading.service.DashboardService;
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
public class DashboardServiceIntegrationTest {

    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private IntegrationTestConfiguration testConfig;
    @Autowired
    private TraderDao traderDao;
    @Autowired
    private AccountDao accountDao;

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
    void getTraderAndAccountTest_ThrowsExceptions() {

        // Trader with ID 0 doesn't exist
        Integer traderId = 0;

        assertThrows(
                ResourceNotFoundException.class,
                () -> dashboardService.getTraderAndAccount(traderId)
        );
    }

    @Test
    void getTraderAndAccountTest_ReturnView() {

        // Get the existing traders
        List<Integer> traderIds = new ArrayList<>();
        traderDao.findAll().forEach((trader) -> traderIds.add(trader.getId()));

        traderIds.forEach((id) -> {
            assertEquals(id, dashboardService.getTraderAndAccount(id).getTrader().getId());
        });
    }

    @Test
    void getPortfolioByTraderIdTest_ThrowException() {

        // Trader with ID 0 doesn't exist
        Integer traderId = 0;

        assertThrows(
                ResourceNotFoundException.class,
                () -> dashboardService.getPortfolioByTraderId(traderId)
        );
    }

    @Test
    void getPortfolioByTraderIdTest_ReturnPortfolios() {

        // Get the existing traders
        List<Integer> traderIds = new ArrayList<>();
        traderDao.findAll().forEach((trader) -> traderIds.add(trader.getId()));

        // All Traders will have a portfolio (Because of test data setup)
        assertEquals(1, dashboardService.getPortfolioByTraderId(traderIds.get(0)).getSecurityRows().size());
        assertEquals(1, dashboardService.getPortfolioByTraderId(traderIds.get(1)).getSecurityRows().size());
        assertEquals(1, dashboardService.getPortfolioByTraderId(traderIds.get(2)).getSecurityRows().size());
    }

    @Test
    void getPortfolioByTraderIdTest_ReturnsEmptyPortfolio() {

        // Adding a new trader and an account (Initially with 0 balance)
        Trader testTrader = new Trader(null, "Mackenzie", "Johnson", Date.valueOf("2000-01-01"),
                "Canada", "mackenzie.johnson@gmail.com");
        testTrader = traderDao.save(testTrader);

        Account testAccount = new Account(null, testTrader.getId(), 0);
        testAccount = accountDao.save(testAccount);

        // Newly added trader with 0 funds and no security order record will have an empty portfolio
        assertEquals(0, dashboardService.getPortfolioByTraderId(testTrader.getId()).getSecurityRows().size());
    }
}
