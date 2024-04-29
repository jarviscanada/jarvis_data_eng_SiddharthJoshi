package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dto.portfolio.PortfolioView;
import ca.jrvs.apps.trading.dto.TraderAccountView;
import ca.jrvs.apps.trading.dto.portfolio.SecurityRow;
import ca.jrvs.apps.trading.entity.Account;
import ca.jrvs.apps.trading.entity.Position;
import ca.jrvs.apps.trading.entity.Trader;
import ca.jrvs.apps.trading.exceptions.ResourceNotFoundException;
import ca.jrvs.apps.trading.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private TraderDao traderDao;
    @Autowired
    private PositionDao positionDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private QuoteDao quoteDao;

    public TraderAccountView getTraderAndAccount(Integer traderId) {

        Trader trader = traderDao.findById(traderId)
                .orElseThrow(() -> new ResourceNotFoundException("Trader associated with ID " + traderId + " doesn't exist."));

        Account account = accountDao.findByTraderId(traderId)
                .orElseThrow(() -> new ResourceNotFoundException("Account associated with the Trader ID " + traderId + " doesn't exist."));

        return new TraderAccountView(trader, account);
    }

    public PortfolioView getPortfolioByTraderId(Integer traderId) {

        List<SecurityRow> securities = new ArrayList<>();

        // Get the account associated with this traderId
        Account account = accountDao.findByTraderId(traderId)
                .orElseThrow(() -> new ResourceNotFoundException("Account associated with this trader doesn't exist."));

        // Getting all the positions associated with that account
        List<Position> positions = positionDao.findAllByAccountId(account.getId());

        // For each position, get the corresponding quote
        positions.forEach((position) ->
                securities.add(
                        new SecurityRow(position.getIdAndTicker().getTicker(), position, quoteDao.findById(position.getIdAndTicker().getTicker()).get())
                )
        );

        return new PortfolioView(securities);
    }
}
