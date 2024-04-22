package ca.jrvs.apps.trading.dto;

import ca.jrvs.apps.trading.entity.Account;
import ca.jrvs.apps.trading.entity.Trader;

public class TraderAccountView {

    private final Trader trader;
    private final Account account;

    public TraderAccountView(Trader trader, Account account) {
        this.trader = trader;
        this.account = account;
    }

    public Trader getTrader() {
        return trader;
    }

    public Account getAccount() {
        return account;
    }
}
