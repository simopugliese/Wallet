package com.simonepugliese;

import com.simonepugliese.Criptor.CreditCardCriptor;
import com.simonepugliese.Criptor.LoginItemCriptor;
import com.simonepugliese.Criptor.WifiItemCriptor;
import com.simonepugliese.Manager.Manager;
import com.simonepugliese.Saver.CreditCardSaver;
import com.simonepugliese.Saver.DbConnector;
import com.simonepugliese.Saver.LoginItemSaver;
import com.simonepugliese.Saver.WifiItemSaver;

public class main {
    public static void main(String[] args) {
        Manager loginManager = new Manager(new LoginItemCriptor(), new LoginItemSaver());
        Manager creditCardManager = new Manager(new CreditCardCriptor(), new CreditCardSaver());
        Manager wifiManager = new Manager(new WifiItemCriptor(), new WifiItemSaver());
        DbConnector db = DbConnector.getInstance();
    }
}
