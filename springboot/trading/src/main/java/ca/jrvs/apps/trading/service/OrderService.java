package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dto.MarketOrderBuyDto;
import ca.jrvs.apps.trading.dto.MarketOrderSellDto;
import ca.jrvs.apps.trading.dto.OrderStatus;
import ca.jrvs.apps.trading.dto.SellResponseDto;
import ca.jrvs.apps.trading.entity.Account;
import ca.jrvs.apps.trading.entity.Position;
import ca.jrvs.apps.trading.entity.Quote;
import ca.jrvs.apps.trading.entity.SecurityOrder;
import ca.jrvs.apps.trading.exceptions.CannotPerformOperationException;
import ca.jrvs.apps.trading.exceptions.InvalidRequestException;
import ca.jrvs.apps.trading.exceptions.ResourceNotFoundException;
import ca.jrvs.apps.trading.exceptions.UnknownDataException;
import ca.jrvs.apps.trading.repository.AccountDao;
import ca.jrvs.apps.trading.repository.PositionDao;
import ca.jrvs.apps.trading.repository.QuoteDao;
import ca.jrvs.apps.trading.repository.SecurityOrderDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    Logger errorLogger = LoggerFactory.getLogger(QuoteService.class);
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
     *
     * @param marketOrderBuyDto client side request DTO
     * @return instance of the security order
     */
    public SecurityOrder executeBuyMarketOrder(MarketOrderBuyDto marketOrderBuyDto) {

        // Validate ticker, account existence and size
        validateMarketOrderDto(marketOrderBuyDto);

        // Get the account associated with the order (.get() method will always work since already validated above)
        Account account = accountDao.findById(marketOrderBuyDto.getAccountId()).get();

        SecurityOrder order = createBuySecurityOrder(marketOrderBuyDto);

        // Validating the funds
        if (!canAffordTheStocks(marketOrderBuyDto.getSize(), order, account)) {
            order.setStatus(String.valueOf(OrderStatus.CANCELED));
            order.setNotes("Canceled due to lack of funds.");
            securityOrderDao.save(order);
            errorLogger.error("Client side issue. They cannot afford the stocks");
            throw new CannotPerformOperationException("Not enough funds to process your Order. Cancelling the Order.");
        }

        return processBuyTransaction(marketOrderBuyDto, order, account);
    }

    /**
     * sell all the stocks associated with the ticker if exists
     *
     * @param sellOrder client side sell order details
     */
    public SellResponseDto executeSellMarketOrder(MarketOrderSellDto sellOrder) {

        // Validate whether the ticker exists or not in the position / wallet via account ID
        Position position = positionDao.findByAccountIdAndTicker(sellOrder.getAccountId(), sellOrder.getTicker())
                .orElseThrow(() -> new CannotPerformOperationException("Quote which is to be liquidated doesn't exist in this trader's portfolio. Cannot perform sell transaction."));

        if (position.getPosition() == 0) {
            errorLogger.error("Client side issue. They have no stocks of " + sellOrder.getTicker() + ".");
            throw new CannotPerformOperationException("You currently own 0 stocks of " + sellOrder.getTicker() + ". Cannot perform sell transaction.");
        }

        SecurityOrder sellSecurityOrder = createSellSecurityOrder(sellOrder, position);

        // Calculating the amount received by liquidating the stock (Rounding off)
        double amountGained = Math.round(position.getPosition() * sellSecurityOrder.getPrice() * 100.0) / 100.0;

        // Crafting a response DTO for client side
        SellResponseDto sellResponse = processSellTransaction(amountGained, sellOrder, sellSecurityOrder);
        sellResponse.setAmountGained(amountGained);

        return sellResponse;
    }

    /**
     * helper method to validate the client request
     *
     * @param marketOrderBuyDto buy order from the client side
     */
    protected void validateMarketOrderDto(MarketOrderBuyDto marketOrderBuyDto) {

        if (!quoteDao.existsById(marketOrderBuyDto.getTicker())) {
            errorLogger.error("Invalid Company Ticker from the request body.");
            throw new InvalidRequestException("Company ticker seems to be invalid.");
        } else if (marketOrderBuyDto.getSize() < 1) {
            errorLogger.error("Invalid amount of stocks from the request body.");
            throw new InvalidRequestException("Amount of Stocks passed is invalid. Cannot be 0 or negative.");
        } else if (!accountDao.existsById(marketOrderBuyDto.getAccountId())) {
            errorLogger.error("Account with ID " + marketOrderBuyDto.getAccountId() + " doesn't exist. Client Side Issue");
            throw new ResourceNotFoundException("Account associated with ID " + marketOrderBuyDto.getAccountId() + " doesn't exist.");
        }
    }

    /**
     * helper method to create a new sell security order
     *
     * @param sellOrder order to sell from the client side
     * @param position  existing position
     * @return instance of the security order
     */
    protected SecurityOrder createSellSecurityOrder(MarketOrderSellDto sellOrder, Position position) {

        SecurityOrder sellSecurityOrder = new SecurityOrder();

        sellSecurityOrder.setStatus(String.valueOf(OrderStatus.PENDING));

        // Set the amount of stocks to negative so the `position` view balances out
        sellSecurityOrder.setSize(-(int) position.getPosition());

        sellSecurityOrder.setNotes("N/A");
        sellSecurityOrder.setTicker(position.getIdAndTicker().getTicker());

        // Get the quote and set the stock price to bid price from quote table
        // Server side issue because stock isn't supposed to exist in position view it is not in quote table
        Quote quote = quoteDao.findById(position.getIdAndTicker().getTicker())
                .orElseThrow(() -> new UnknownDataException("Server Side issue."));
        sellSecurityOrder.setPrice(quote.getBidPrice());

        sellSecurityOrder.setAccount_id(position.getIdAndTicker().getAccount_id());

        return sellSecurityOrder;
    }

    /**
     * helper method to create a new buy Security Order
     *
     * @param marketOrder order from the client side
     * @return instance of the SecurityOrder
     */
    protected SecurityOrder createBuySecurityOrder(MarketOrderBuyDto marketOrder) {

        SecurityOrder buySecurityOrder = new SecurityOrder();

        buySecurityOrder.setStatus(String.valueOf(OrderStatus.PENDING));
        buySecurityOrder.setSize(marketOrder.getSize());
        buySecurityOrder.setNotes("N/A");
        buySecurityOrder.setTicker(marketOrder.getTicker());

        Quote quote = quoteDao.findById(marketOrder.getTicker())
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find the Quote..."));

        buySecurityOrder.setPrice(quote.getAskPrice());
        buySecurityOrder.setAccount_id(marketOrder.getAccountId());

        return buySecurityOrder;
    }

    /**
     * helper method to check whether the account associated with the trader has enough money to
     * fulfil the order or not.
     *
     * @return true if trader can afford otherwise false
     */
    protected boolean canAffordTheStocks(int size, SecurityOrder order, Account account) {
        return account.getAmount() >= order.getPrice() * size;
    }

    /**
     * Helper method to process the buy transaction
     * Updates the account with the updated amount and saves the order and updated account in the db
     *
     * @param orderData     order from the client side
     * @param securityOrder newly created security order
     */
    protected SecurityOrder processBuyTransaction(MarketOrderBuyDto orderData, SecurityOrder securityOrder, Account account) {

        // Deduct the amount and save the updated account record
        account.setAmount(account.getAmount() - orderData.getSize() * securityOrder.getPrice());
        accountDao.save(account);

        // Save the order in the database after updating the status
        securityOrder.setStatus(String.valueOf(OrderStatus.FILLED));
        securityOrder.setNotes("BOUGHT");
        return securityOrderDao.save(securityOrder);
    }

    protected SellResponseDto processSellTransaction(double fundsGained, MarketOrderSellDto sellOrder, SecurityOrder sellSecurityOrder) {

        // Potential exception because position should not exist if there is no account associated with it
        Account account = accountDao.findById(sellSecurityOrder.getAccount_id())
                .orElseThrow(() -> new UnknownDataException("Something is wrong from the server side."));

        // Update the funds in the account by adding (Rounding off)
        account.setAmount(Math.round((account.getAmount() + fundsGained) * 100.0) / 100.0);
        accountDao.save(account);

        // Update Security Order
        sellSecurityOrder.setNotes("SOLD");
        sellSecurityOrder.setStatus(String.valueOf(OrderStatus.FILLED));
        securityOrderDao.save(sellSecurityOrder);

        SellResponseDto sellResponse = new SellResponseDto();
        sellResponse.setMessage("Updated Funds in Account with ID " + account.getId() + ": $" + account.getAmount());

        return sellResponse;
    }
}
