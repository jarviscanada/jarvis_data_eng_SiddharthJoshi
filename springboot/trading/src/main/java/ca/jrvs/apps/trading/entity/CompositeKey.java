package ca.jrvs.apps.trading.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class CompositeKey implements Serializable {

    private int account_id;
    private String ticker;

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
}
