package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.entity.Account;
import ca.jrvs.apps.trading.entity.SecurityOrder;
import ca.jrvs.apps.trading.entity.Trader;
import ca.jrvs.apps.trading.dto.TraderAccountView;
import ca.jrvs.apps.trading.exceptions.CannotPerformOperationException;
import ca.jrvs.apps.trading.exceptions.InvalidRequestException;
import ca.jrvs.apps.trading.exceptions.ResourceNotFoundException;
import ca.jrvs.apps.trading.exceptions.UnknownDataException;
import ca.jrvs.apps.trading.repository.AccountDao;
import ca.jrvs.apps.trading.repository.PositionDao;
import ca.jrvs.apps.trading.repository.SecurityOrderDao;
import ca.jrvs.apps.trading.repository.TraderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TraderAccountService {

    @Autowired
    private TraderDao traderDao;
    @Autowired
    private SecurityOrderDao securityOrderDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private PositionDao positionDao;

    /**
     * creates a new trader and initializes a new account associated with the newly created trader
     * with 0 amount
     * @param trader trader to be added (non-null except for id)
     * @return traderAccountView
     * @throws IllegalArgumentException if a trader has null fields or id is not null
     */
    public TraderAccountView createTraderAndAccount(Trader trader) {

        if (areTraderFieldsNull(trader)) {
            throw new InvalidRequestException("Bad Request. Data fields associated with the Trader must not be null.");
        }

        // Add a new trader account record in the database
        Trader newTrader = traderDao.save(trader);

        // Create a new account associated with the newly created trader
        Account newAccount = createAccountEntity(newTrader);
        accountDao.save(newAccount);

        return new TraderAccountView(trader, newAccount);
    }

    /**
     * deletes a trader associated with a specific id.
     * A trader can only be deleted if and only if it has no open position and 0 cash balance
     * @param traderId id of the trader (not null)
     * @throws IllegalArgumentException if traderId is null, or not found or unable to delete
     */
    public void deleteTraderById(Integer traderId) {

        if (traderId == null) {
            throw new InvalidRequestException("Invalid Request. Trader ID cannot be null.");
        }

        if (!traderDao.existsById(traderId)) {
            throw new ResourceNotFoundException(
                    "Cannot delete the trader. Trader associated with the id " + traderId + " doesn't exist inside the database."
            );
        }

        Optional<Account> optionalAccount = accountDao.findByTraderId(traderId);
        if (optionalAccount.isEmpty()) {
            throw new UnknownDataException("Sorry, we are experiencing some issues within our services. Please give us a moment while we work to fix it.");
        }

        Account account = optionalAccount.get();
        if (account.getAmount() != 0) {
            throw new CannotPerformOperationException(
                    "Error. Cannot delete the trader as the account associated with that trader still has funds."
            );
        }

        // After all these checks, account is now eligible for deletion

        // First, delete all the orders associated with the account. Position view will get updated as security_order table gets modified
        List<SecurityOrder> orders = securityOrderDao.findAllByAccountId(account.getId());
        orders.forEach((order) -> securityOrderDao.deleteById(order.getAccount_id()));

        // After that, delete the account associated with the trader
        accountDao.deleteById(account.getTrader_id());

        // Lastly, delete the trader itself
        traderDao.deleteById(traderId);
    }

    /**
     * deposit funds to an account associated with a specific traderId
     * @param traderId id of a trader - not null
     * @param funds amount to be deposited - greater than 0
     * @return updated account details
     * @throws IllegalArgumentException if traderId is null or not found, and fund is less than
     * or equal to 0
     */
    public Account depositFunds(Integer traderId, Double funds) {

        validateBeforeTransaction(traderId, funds);

        Optional<Account> accountOptional = accountDao.findByTraderId(traderId);

        if (accountOptional.isEmpty()) {
            throw new CannotPerformOperationException("Operation failed. Account and Trader associated with " + traderId + " doesn't exist in the database.");
        }

        Account account = accountOptional.get();

        // Update the funds in DTO / entity
        account.setAmount(funds + account.getAmount());

        return accountDao.save(account);
    }

    /**
     * withdraw funds from an account associated with a specific traderId
     * @param traderId id of a trader - not null
     * @param funds amount to be withdrawn - greater than 0
     * @return updated account details
     * @throws IllegalArgumentException if traderId is null or not found, and fund is less than
     * or equal to 0
     */
    public Account withdrawFunds(Integer traderId, Double funds) {

        validateBeforeTransaction(traderId, funds);

        Optional<Account> accountOptional = accountDao.findByTraderId(traderId);
        if (accountOptional.isEmpty()) {
            throw new CannotPerformOperationException("Operation failed. Account and Trader associated with " + traderId + " doesn't exist in the database.");
        }

        Account account = accountOptional.get();
        double remainingBalance = account.getAmount() - funds;
        if (remainingBalance < 0) {
            throw new CannotPerformOperationException("Operation failed. Insufficient funds in the account with ID ");
        }

        account.setAmount(remainingBalance);
        return accountDao.save(account);
    }

    /**
     * helper method which validates id and funds
     * @param id trader id or account id - must not be null
     * @param funds funds to deposit or withdraw - must not be 0
     */
    private void validateBeforeTransaction(Integer id, Double funds) {

        if (id == null) {
            throw new InvalidRequestException("Invalid Request. Trader ID cannot be null.");
        }

        else if (funds < 1) {
            throw new InvalidRequestException("Invalid Request. Funds must be greater than 0.");
        }
    }

    /**
     * helper method which creates an account with 0 funds associated with a newly created trader
     * @param trader newly created trader
     * @return instance of the newly created account
     */
    private Account createAccountEntity(Trader trader) {
        return new Account(null, trader.getId(), 0);
    }

    /**
     * helper method to validate whether fields associated with the trader entity are null or not
     * @param trader trader entity
     * @return true if any null value is present otherwise false
     */
    private boolean areTraderFieldsNull(Trader trader) {
        return trader.getDob() == null || trader.getCountry() == null || trader.getEmail() == null
                || trader.getFirst_name() == null || trader.getLast_name() == null;
    }
}
