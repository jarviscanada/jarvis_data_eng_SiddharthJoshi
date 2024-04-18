package ca.jrvs.apps.trading.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Account {

    public Account() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private int trader_id;
    private double amount;

    public Account(Integer id, int trader_id, double amount) {
        this.id = id;
        this.trader_id = trader_id;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrader_id() {
        return trader_id;
    }

    public void setTrader_id(int trader_id) {
        this.trader_id = trader_id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
