package com.simonepugliese.Saver;

import com.simonepugliese.Item.CreditCardItem;
import com.simonepugliese.Item.Item;

import java.util.List;

public class CreditCardSaver extends Saver {
    @Override
    public void salva(Item item) {
        CreditCardItem creditCardItem = (CreditCardItem) item;
        System.out.println("Ho salvato la CreditCardItem " + item.getDescription());
    }

    @Override
    public List<Item> carica() {
        System.out.println("Ho letto i dati");
        return List.of();
    }
}
