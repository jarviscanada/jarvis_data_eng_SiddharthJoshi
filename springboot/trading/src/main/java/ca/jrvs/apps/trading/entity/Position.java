

package ca.jrvs.apps.trading.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Immutable;

@Entity
public class Position {

    public Position() {

    }

    @EmbeddedId
    private CompositeKey idAndTicker;
    private double position;

    public CompositeKey getIdAndTicker() {
        return idAndTicker;
    }

    public double getPosition() {
        return position;
    }

    // Exclude Setters
}

