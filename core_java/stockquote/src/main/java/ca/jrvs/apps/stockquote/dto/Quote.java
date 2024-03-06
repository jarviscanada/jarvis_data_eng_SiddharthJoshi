package ca.jrvs.apps.stockquote.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "01. symbol",
    "02. open",
    "03. high",
    "04. low",
    "05. price",
    "06. volume",
    "07. latest trading day",
    "08. previous close",
    "09. change",
    "10. change percent",
})
public class Quote {

    @JsonProperty("01. symbol")
    private String ticker;
    @JsonProperty("02. open")
    private double open;
    @JsonProperty("03. high")
    private double high;
    @JsonProperty("04. low")
    private double low;
    @JsonProperty("05. price")
    private double price;
    @JsonProperty("06. volume")
    private int volume;
    @JsonProperty("07. latest trading day")
    private String latestTradingDay;
    @JsonProperty("08. previous close")
    private double previousClose;
    @JsonProperty("09. change")
    private double change;
    @JsonProperty("10. change percent")
    private String changePercent;
    @JsonIgnore
    private Timestamp timestamp; //time when the info was pulled

    public Quote() {

    }

    public Quote(String ticker, double open, double high, double low, double price, int volume,
        String latestTradingDay, double previousClose, double change, String changePercent,
        Timestamp timestamp) {
        this.ticker = ticker;
        this.open = open;
        this.high = high;
        this.low = low;
        this.price = price;
        this.volume = volume;
        this.latestTradingDay = latestTradingDay;
        this.previousClose = previousClose;
        this.change = change;
        this.changePercent = changePercent;
        this.timestamp = timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return
            "Company Symbol: " + ticker +
            "\nOpen: " + open +
            "\nHigh: " + high +
            "\nLow: " + low +
            "\nPrice: " + price +
            "\nVolume: " + volume +
            "\nLatest Trading Day: " + latestTradingDay +
            "\nPrevious Close: " + previousClose +
            "\nChange: " + change +
            "\nChange in Percentage: " + changePercent +
            "\nTimestamp: " + timestamp.toString();
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setLatestTradingDay(String latestTradingDay) {
        this.latestTradingDay = latestTradingDay;
    }

    public void setPreviousClose(double previousClose) {
        this.previousClose = previousClose;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }

    public String getTicker() {
        return ticker;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getPrice() {
        return price;
    }

    public int getVolume() {
        return volume;
    }

    public String getLatestTradingDay() {
        return latestTradingDay;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public double getChange() {
        return change;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Quote quote = (Quote) object;

        if (Double.compare(getOpen(), quote.getOpen()) != 0) {
            return false;
        }
        if (Double.compare(getHigh(), quote.getHigh()) != 0) {
            return false;
        }
        if (Double.compare(getLow(), quote.getLow()) != 0) {
            return false;
        }
        if (Double.compare(getPrice(), quote.getPrice()) != 0) {
            return false;
        }
        if (getVolume() != quote.getVolume()) {
            return false;
        }
        if (Double.compare(getPreviousClose(), quote.getPreviousClose()) != 0) {
            return false;
        }
        if (Double.compare(getChange(), quote.getChange()) != 0) {
            return false;
        }
        if (!getTicker().equals(quote.getTicker())) {
            return false;
        }
        if (!getLatestTradingDay().equals(quote.getLatestTradingDay())) {
            return false;
        }
        if (!getChangePercent().equals(quote.getChangePercent())) {
            return false;
        }
        return getTimestamp().equals(quote.getTimestamp());
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getTicker().hashCode();
        temp = Double.doubleToLongBits(getOpen());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getHigh());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getLow());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getPrice());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getVolume();
        result = 31 * result + getLatestTradingDay().hashCode();
        temp = Double.doubleToLongBits(getPreviousClose());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getChange());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getChangePercent().hashCode();
        result = 31 * result + getTimestamp().hashCode();
        return result;
    }
}
