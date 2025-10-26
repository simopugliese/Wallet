package com.simonepugliese.Data;

public class CreditCardItem extends Item {
    private String owner;
    private String bank;
    private String number;
    private int cvv;
    private String expiration;

    public CreditCardItem(
            int id,
            String owner,
            String bank,
            String number,
            int cvv,
            String expiration,
            String notes
    ) {
        super(id, ItemType.CREDITCARD, notes);
        this.owner = owner;
        this.bank = bank;
        this.number = number;
        this.cvv = cvv;
        this.expiration = expiration;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }
}
