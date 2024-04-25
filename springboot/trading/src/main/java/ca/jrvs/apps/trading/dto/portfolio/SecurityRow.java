package ca.jrvs.apps.trading.dto.portfolio;

import ca.jrvs.apps.trading.entity.Position;
import ca.jrvs.apps.trading.entity.Quote;

public class SecurityRow {

    private String ticker;
    private Position position;
    private Quote quote;

    public SecurityRow() {

    }

    public SecurityRow(String ticker, Position position, Quote quote) {
        this.ticker = ticker;
        this.position = position;
        this.quote = quote;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Quote getQuote() {
        return quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }
}
