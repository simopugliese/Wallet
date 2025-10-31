package com.simonepugliese;

import com.simonepugliese.Criptor.CreditCardCriptor;
import com.simonepugliese.Criptor.LoginItemCriptor;
import com.simonepugliese.Item.CreditCardItem;
import com.simonepugliese.Item.Item;
import com.simonepugliese.Item.LoginItem;
import com.simonepugliese.Manager.Manager;
import com.simonepugliese.Saver.CreditCardSaver;
import com.simonepugliese.Saver.LoginItemSaver;

import java.util.List;

public class App {
    public static void main(String[] args) {
        Manager managerLoginItem = new Manager(new LoginItemCriptor(), new LoginItemSaver());
        Manager managerCreditCardItem = new Manager(new CreditCardCriptor(), new CreditCardSaver());

        LoginItem loginItem = new LoginItem("Google", "simone", "ciao", "www.google.com");
        CreditCardItem creditCardItem = new CreditCardItem("mia", "mia", "banca", "12344560", 213);

        managerLoginItem.criptaPoiSalva(loginItem);
        List<Item> items = managerLoginItem.caricaPoiDecripta();
        items.forEach(System.out::println);
        managerCreditCardItem.criptaPoiSalva(creditCardItem);
        List<Item> itemsCredit = managerCreditCardItem.caricaPoiDecripta();
        itemsCredit.forEach(System.out::println);
    }
}
