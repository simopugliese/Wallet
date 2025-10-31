package com.simonepugliese.Criptor;

import com.simonepugliese.Item.CreditCardItem;
import com.simonepugliese.Item.Item;

public class CreditCardCriptor extends Criptor {
    @Override
    public Item cripta(Item item) {
        CreditCardItem creditCardItem = (CreditCardItem) item;
        String owner = CryptoUtils.encrypt(creditCardItem.getOwner());
        String bank = CryptoUtils.encrypt(creditCardItem.getBank());
        String number = CryptoUtils.encrypt(creditCardItem.getNumber());
        String cvv = CryptoUtils.encrypt(creditCardItem.getCvv());
        if (owner != null && bank != null && number != null && cvv != null) {
            creditCardItem.setOwner(owner);
            creditCardItem.setBank(bank);
            creditCardItem.setNumber(number);
            creditCardItem.setCvv(cvv);
            return creditCardItem;
        }
        return null;
    }

    @Override
    public Item decripta(Item item) {
        CreditCardItem creditCardItem = (CreditCardItem) item;
        String owner = CryptoUtils.decrypt(creditCardItem.getOwner());
        String bank = CryptoUtils.decrypt(creditCardItem.getBank());
        String number = CryptoUtils.decrypt(creditCardItem.getNumber());
        String cvv = CryptoUtils.decrypt(creditCardItem.getCvv());
        if (owner != null && bank != null && number != null && cvv != null) {
            creditCardItem.setOwner(owner);
            creditCardItem.setBank(bank);
            creditCardItem.setNumber(number);
            creditCardItem.setCvv(cvv);
            return creditCardItem;
        }
        return null;
    }
}
