package vn.edu.fpt.model.dto;


import java.time.LocalDateTime;

public class Card {
    private String cardNumber;
    private String expirationDate;
    private String fullName;

    public Card() {
    }

    public Card(String cartNumber, String expirationDate, String fullName) {
        this.cardNumber = cartNumber;
        this.expirationDate = expirationDate;
        this.fullName = fullName;
    }

    public String getCartNumber() {
        return cardNumber;
    }

    public void setCartNumber(String cartNumber) {
        this.cardNumber = cartNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardNumber='" + cardNumber + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
