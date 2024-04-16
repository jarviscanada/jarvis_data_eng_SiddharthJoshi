package ca.jrvs.apps.trading.marketdata.config;

import ca.jrvs.apps.trading.domain.Quote;
import ca.jrvs.apps.trading.repository.QuoteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("test")
public class IntegrationTestConfiguration {

    @Autowired
    private QuoteDao quoteDao;

    public void setUpTestData() {
        Quote testQuoteOne = new Quote("TEST_ONE", 1000D, 995D,
                2L, 1001D, 2L);

        Quote testQuoteTwo = new Quote("TEST_TWO", 2000D, 1995D,
                2L, 2001D, 2L);

        Quote testQuoteThree = new Quote("TEST_THREE", 3000D, 2995D,
                2L, 3001D, 2L);

        quoteDao.save(testQuoteOne);
        quoteDao.save(testQuoteTwo);
        quoteDao.save(testQuoteThree);
    }

    public void cleanUpTestData() {
        quoteDao.deleteAll();
    }
}
