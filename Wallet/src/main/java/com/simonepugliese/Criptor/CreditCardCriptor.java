package com.simonepugliese.Criptor;

import com.simonepugliese.Item.CreditCardItem;
import com.simonepugliese.Item.Item;

public class CreditCardCriptor extends Criptor {
    @Override
    public Item cripta(Item item) {
        CreditCardItem creditCardItem = (CreditCardItem) item;
        return creditCardItem;
    }

    @Override
    public Item decripta(Item item) {
        CreditCardItem creditCardItem = (CreditCardItem) item;
        return creditCardItem;
    }
}
