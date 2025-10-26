package com.simonepugliese;

import com.simonepugliese.Data.CreditCardItem;
import com.simonepugliese.Data.Item;
import com.simonepugliese.Data.WifiBasicItem;

public class main {
    public static void main(String[] args) {
        Item cartaUno = new CreditCardItem(1, "simo", "banca", "0000", 124, "dic 2056", "no notes");
        Item basicWifi = new WifiBasicItem(2, "ciao", "ciao2", "non so che wifi sia");

    }
}
