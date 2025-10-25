package com.simonepugliese;

import com.simonepugliese.core.model.Item.BasicWifiFactory;
import com.simonepugliese.core.model.Item.CreditCardFactory;
import com.simonepugliese.core.model.Item.ItemFactory;

public class main {
    public static void main(String[] args) {
        int idUno = 1;
        int idDue = 2;

        ItemFactory creditCard = new CreditCardFactory((Object) idUno, "simone", "hype", "1234123412341234",123,"Dic 2025","mia carta");
        CreditCardFactory c = (CreditCardFactory) creditCard;
        System.out.println(c.toString());

        ItemFactory basicWifi = new BasicWifiFactory((Object) idDue, "ciao", "password","casa");
        BasicWifiFactory w = (BasicWifiFactory) basicWifi;
        System.out.println(w.toString());
    }
}
