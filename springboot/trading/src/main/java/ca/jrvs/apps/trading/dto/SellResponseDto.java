package ca.jrvs.apps.trading.dto;

public class SellResponseDto {

    private String message;
    private double amountGained;

    public SellResponseDto() {

    }

    public SellResponseDto(String message, double amountGained) {
        this.message = message;
        this.amountGained = amountGained;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getAmountGained() {
        return amountGained;
    }

    public void setAmountGained(double amountGained) {
        this.amountGained = amountGained;
    }
}
