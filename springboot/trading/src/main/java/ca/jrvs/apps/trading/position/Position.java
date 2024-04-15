package ca.jrvs.apps.trading.position;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
public class Position {

    @Id
    private Integer account_id;
    private String ticker;
    private double position;

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public String getTicker() {
        return ticker;
    }

    // Exclude Setters
}
