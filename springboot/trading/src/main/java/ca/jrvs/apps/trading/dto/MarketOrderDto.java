package ca.jrvs.apps.trading.dto;

public class MarketOrderDto {

    private int accountId;
    private int size;
    private String ticker;
    private String orderType;
    // one more field `option`

    public MarketOrderDto() {

    }

    public MarketOrderDto(int accountId, int size, String ticker, String orderType) {
        this.accountId = accountId;
        this.size = size;
        this.ticker = ticker;
        this.orderType = orderType;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
