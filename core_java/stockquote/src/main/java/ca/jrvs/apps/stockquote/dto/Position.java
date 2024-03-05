package ca.jrvs.apps.stockquote.dto;

public class Position {

    private String ticker;
    private int numOfShares;
    private double valuePaid;

    public Position() {

    }

    public Position(String ticker, int numOfShares, double valuePaid) {
        this.ticker = ticker;
        this.numOfShares = numOfShares;
        this.valuePaid = valuePaid;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Position position = (Position) object;

        if (numOfShares != position.numOfShares) {
            return false;
        }
        if (Double.compare(valuePaid, position.valuePaid) != 0) {
            return false;
        }
        return ticker.equals(position.ticker);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = ticker.hashCode();
        result = 31 * result + numOfShares;
        temp = Double.doubleToLongBits(valuePaid);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getNumOfShares() {
        return numOfShares;
    }

    public void setNumOfShares(int numOfShares) {
        this.numOfShares = numOfShares;
    }

    public double getValuePaid() {
        return valuePaid;
    }

    public void setValuePaid(double valuePaid) {
        this.valuePaid = valuePaid;
    }

    @Override
    public String toString() {
        return "Position{" +
            "ticker='" + ticker + '\'' +
            ", numOfShares=" + numOfShares +
            ", valuePaid=" + valuePaid +
            '}';
    }
}
