package ca.jrvs.apps.trading.dto;

public class MarketOrderSellDto {

    private String ticker;
    private Integer accountId;

    MarketOrderSellDto() {

    }

    public MarketOrderSellDto(Integer accountId, String ticker) {
        this.accountId = accountId;
        this.ticker = ticker;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}
