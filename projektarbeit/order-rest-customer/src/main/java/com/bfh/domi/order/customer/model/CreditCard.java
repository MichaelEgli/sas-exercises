package com.bfh.domi.order.customer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class CreditCard {

    @Enumerated(EnumType.STRING)
    @Column(name = "CREDIT_CARD_TYPE")
    private CreditCardType type;
    @Column(name = "CREDIT_CARD_NUMBER")
    private String number;
    @Column(name = "CREDIT_CARD_EXPIRATION_MONTH")
    private int expirationMonth;
    @Column(name = "CREDIT_CARD_EXPIRATION_YEAR")
    private int expirationYear;

    public CreditCardType getType() {
        return type;
    }

    public void setType(CreditCardType type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(int expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public int getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(int expirationYear) {
        this.expirationYear = expirationYear;
    }
}
