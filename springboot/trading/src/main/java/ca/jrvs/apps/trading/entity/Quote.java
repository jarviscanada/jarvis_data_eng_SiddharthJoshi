package ca.jrvs.apps.trading.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Quote {

    // Primary Key
    @Id
    private String ticker;

    private Double lastPrice;
    private Double bidPrice;
    private Long bidSize;
    private Double askPrice;
    private Long askSize;

    public Quote() {

    }

    public Quote(String ticker, Double lastPrice, Double bidPrice, Long bidSize, Double askPrice, Long askSize) {
        this.ticker = ticker;
        this.lastPrice = lastPrice;
        this.bidPrice = bidPrice;
        this.bidSize = bidSize;
        this.askPrice = askPrice;
        this.askSize = askSize;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public Double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(Double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public Long getBidSize() {
        return bidSize;
    }

    public void setBidSize(Long bidSize) {
        this.bidSize = bidSize;
    }

    public Double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(Double askPrice) {
        this.askPrice = askPrice;
    }

    public Long getAskSize() {
        return askSize;
    }

    public void setAskSize(Long askSize) {
        this.askSize = askSize;
    }
}
