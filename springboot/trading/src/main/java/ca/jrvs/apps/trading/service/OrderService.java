package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dto.MarketOrderDto;
import ca.jrvs.apps.trading.dto.OrderStatus;
import ca.jrvs.apps.trading.entity.Account;
import ca.jrvs.apps.trading.entity.Quote;
import ca.jrvs.apps.trading.entity.SecurityOrder;
import ca.jrvs.apps.trading.exceptions.CannotPerformOperationException;
import ca.jrvs.apps.trading.exceptions.InvalidRequestException;
import ca.jrvs.apps.trading.exceptions.ResourceNotFoundException;
import ca.jrvs.apps.trading.repository.AccountDao;
import ca.jrvs.apps.trading.repository.PositionDao;
import ca.jrvs.apps.trading.repository.QuoteDao;
import ca.jrvs.apps.trading.repository.SecurityOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private AccountDao accountDao;
    @Autowired
    private SecurityOrderDao securityOrderDao;
    @Autowired
    private QuoteDao quoteDao;
    @Autowired
    private PositionDao positionDao;

    /**
     * this function executes the market order saving it inside the database in the logical order
     * @param marketOrderDto client side request DTO
     * @return instance of the security order
     */
    public SecurityOrder executeMarketOrder(MarketOrderDto marketOrderDto) {

        // Validate ticker, account existence and size
        validateMarketOrderDto(marketOrderDto);

        // Get the account associated with the order (.get() method will always work since already validated above)
        Account account = accountDao.findById(marketOrderDto.getAccountId()).get();

        // Creating a new security order (Initial status will be 'PENDING')
        SecurityOrder order = createSecurityOrder(marketOrderDto);

        // Validate the funds
        if (!canAffordTheStocks(marketOrderDto.getSize(), order, account)) {
            order.setStatus(String.valueOf(OrderStatus.CANCELED));
            order.setNotes("Canceled due to lack of funds.");
            securityOrderDao.save(order);
            throw new CannotPerformOperationException("Not enough funds to process your Order. Cancelling the Order.");
        }

        return processBuyTransaction(marketOrderDto, order, account);
    }

    /**
     * helper method to validate the client request
     * @param marketOrderDto order from the client side
     */
    protected void validateMarketOrderDto(MarketOrderDto marketOrderDto) {

        if (!quoteDao.existsById(marketOrderDto.getTicker())) {
            throw new InvalidRequestException("Company ticker seems to be invalid.");
        }
        else if (marketOrderDto.getSize() < 1) {
            throw new InvalidRequestException("Amount of Stocks passed is invalid. Cannot be 0 or negative.");
        }
        else if (!accountDao.existsById(marketOrderDto.getAccountId())) {
            throw new ResourceNotFoundException("Account associated with ID " + marketOrderDto.getAccountId() + " doesn't exist.");
        }
        // Todo: Add one more check to validate size against the actual existing volumes (amount of shares)
    }

    /**
     * helper method to create a new Security Order
     * @param marketOrder order from the client side
     * @return instance of the SecurityOrder
     */
    protected SecurityOrder createSecurityOrder(MarketOrderDto marketOrder) {

        SecurityOrder securityOrder = new SecurityOrder();

        securityOrder.setStatus(String.valueOf(OrderStatus.PENDING));
        securityOrder.setSize(marketOrder.getSize());
        securityOrder.setNotes("N/A");
        securityOrder.setTicker(marketOrder.getTicker());

        Quote quote = quoteDao.findById(marketOrder.getTicker())
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find the Quote..."));

        securityOrder.setPrice(quote.getAskPrice());
        securityOrder.setAccount_id(marketOrder.getAccountId());

        return securityOrder;
    }

    /**
     * helper method to check whether the account associated with the trader has enough money to
     * fulfil the order or not.
     * @return true if trader can afford otherwise false
     */
    protected boolean canAffordTheStocks(int size, SecurityOrder order, Account account) {
        return account.getAmount() >= order.getPrice() * size;
    }

    /**
     * Helper method to handle a buy request
     * Updates the account with the updated amount and saves the order and updated account in the db
     * @param orderData order from the client side
     * @param securityOrder newly created security order
     */
    protected SecurityOrder processBuyTransaction(MarketOrderDto orderData, SecurityOrder securityOrder, Account account) {

        // Deduct the amount and save the updated account record
        account.setAmount(account.getAmount() - orderData.getSize() * securityOrder.getPrice());
        accountDao.save(account);

        // Save the order in the database after updating the status
        securityOrder.setStatus(String.valueOf(OrderStatus.FILLED));
        securityOrder.setNotes("Bought");
        return securityOrderDao.save(securityOrder);
    }

    protected void handleSellMarketOrder(MarketOrderDto orderData, SecurityOrder securityOrder, Account account) {

    }
}
